package com.giorgioinf.twtml.spark

import com.giorgioinf.twtml.web.WebClient
import com.typesafe.config.ConfigFactory
import java.text.Normalizer
import org.apache.spark.{Logging, SparkConf}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.{SparseVector, DenseVector, Vector, Vectors}
import org.apache.spark.mllib.regression.{LabeledPoint, StreamingLinearRegressionWithSGD}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.viz.lightning.Lightning
import scala.concurrent.duration._
import scala.math.BigDecimal
import scala.util.Try
import twitter4j.Status

object LinearRegression extends Logging {

  // setup
  val timing = Seconds(2)
  val numRetweetBegin = 10
  val numRetweetEnd = 10000
  val numTextFeatures = 1000000
  val numIterations = 50
  val stepSize = 0.01
  val miniBatchFraction = 1.0

  // blue
  val realColorDet = Array(173,216,230)
  val realColor = Array(30,144,255)
  // yellow
  val predColorDet = Array(238,232,170)
  val predColor = Array(255,215,0)

  val numNumberFeatures = 4
  val numFeatures = numTextFeatures + numNumberFeatures
  val numberFeatureIndices = (numTextFeatures to numFeatures-1).toArray
  val hashText = new HashingTF(numTextFeatures)



  /**
   * Create feature vectors by turning each tweet into bigrams of
   * characters (an n-gram model) and then hashing those to a
   * length-1000 feature vector that we can pass to MLlib.
   * This is a common way to decrease the number of features in a
   * model while still getting excellent accuracy (otherwise every
   * pair of Unicode characters would potentially be a feature).
   */
  def featurizeText(statuses: Status): SparseVector = {
    val text = statuses.getRetweetedStatus
      .getText
      .toLowerCase

    // Separate accents from characters and then remove non-unicode
    // characters
    val noAccentText = Normalizer
      .normalize(text, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")

    // bigrams
    hashText.transform(text.sliding(2).toSeq)
      .asInstanceOf[SparseVector]
  }

  def featurizeNumbers(statuses: Status): Vector = {
    val user = statuses.getRetweetedStatus.getUser
    val created = statuses.getRetweetedStatus.getCreatedAt
    val timeLeft = (System.currentTimeMillis - created.getTime)

    Vectors.dense(
      user.getFollowersCount * Math.pow(10, -12),
      user.getFavouritesCount * Math.pow(10, -12),
      user.getFriendsCount * Math.pow(10, -12),
      timeLeft * Math.pow(10, -14)
      //retweeted.getURLEntities.length,
      //retweeted.getUserMentionEntities.length
    )
  }

  def featurize(statuses: Status): LabeledPoint = {
    val textFeatures = featurizeText(statuses)
    val numberFeatures = featurizeNumbers(statuses)
    val features = Vectors.sparse(
      numFeatures,
      textFeatures.indices ++ numberFeatureIndices,
      textFeatures.values ++ numberFeatures.toArray
    )
    LabeledPoint( statuses.getRetweetedStatus.getRetweetCount.toDouble, features )
  }

  def filtrate(statuses: Status): Boolean = {
    (
      statuses.isRetweet &&
      statuses.getLang == "en" &&
      retweetInterval(statuses, numRetweetBegin, numRetweetEnd)
    )
  }

  def retweetInterval(statuses: Status, start:Long, end:Long):Boolean = {
    val n = statuses.getRetweetedStatus.getRetweetCount
    (n >= start && n <= end)
  }

  def round(number:Double):Double = {
    BigDecimal(number).setScale(0, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def main(args: Array[String]) {

    log.info("Loading application config...")

    val conf = ConfigFactory.load
    val lgn = Lightning(conf.getString("lightning"))
    val web = WebClient(conf.getString("twtweb"))

    log.info("Parsing applications arguments")

    val sparkConf = ConfArguments.parse(args.toList, new SparkConf()
      .setMaster("local[2]")
      .setAppName("twitter-stream-ml-linear-regression"))

    log.info("Initializing Lightning graph session...")
    lgn.createSession(sparkConf.get("spark.app.name"))

    val graph = lgn.linestreaming(
        Array.fill(4)(Array(0.0)),
        size=Array(2, 2, 4, 4),
        color=Array(realColorDet, predColorDet, realColor, predColor))

    Try(web.config(lgn.session, lgn.host, List(graph.id)))

    log.info("Initializing Spark Machine Learning Model...")

    val initialWeights = Vectors.zeros(numFeatures)

    val model = new StreamingLinearRegressionWithSGD()
      .setNumIterations(numIterations)
      .setStepSize(stepSize)
      .setMiniBatchFraction(miniBatchFraction)
      .setInitialWeights(initialWeights)

    log.info("Initializing Streaming Spark Context...")

    val ssc = new StreamingContext(sparkConf, timing)

    log.info("Initializing Twitter stream...")

    val stream = TwitterUtils.createStream(ssc, None)
      .filter(filtrate)
      .map(featurize)
      .cache()

    var count = 0L
    stream.foreachRDD({ rdd =>
      if (!rdd.isEmpty) {
        val batch = rdd.count
        count += batch

        val realAndPred = rdd.map{ lb =>
          (lb.label, round(model.latestModel.predict(lb.features)))
        }
        val real = realAndPred.map(_._1)
        val pred = realAndPred.map(_._2)
        val realStdev = round(real.stdev)
        val predStdev = round(pred.stdev)
        val realStdevArr = Array.fill(batch.toInt)(realStdev)
        val predStdevArr = Array.fill(batch.toInt)(predStdev)
        val mse = round(realAndPred.map{case(v, p) => math.pow((v - p), 2)}.mean())

        if (log.isDebugEnabled) {
          // batch, training mean squared error
          log.debug("count: {}", count)
          log.debug("batch: {},  mse: {}", batch, mse.toLong)
          log.debug("stdev (real, pred): ({}, {})", realStdev, predStdev)
          log.debug("value (real, pred): {} ...", realAndPred.take(9).toArray)
        }

        Try(web.stats(count))
        Try(graph.append( Array(real.toArray, pred.toArray, realStdevArr, predStdevArr )))
      } else {
        log.debug("batch: 0")
      }
    })

    model.trainOn(stream)




    // Start the streaming computation
    ssc.start()
    log.info("Initialization complete.")
    ssc.awaitTermination()
  }

}
