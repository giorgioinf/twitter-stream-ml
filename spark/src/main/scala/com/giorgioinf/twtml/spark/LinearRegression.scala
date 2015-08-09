package com.giorgioinf.twtml.spark

import com.typesafe.config.ConfigFactory
import org.apache.spark.{Logging, SparkConf}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.StreamingLinearRegressionWithSGD
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils

object LinearRegression extends Logging {

  def main(args: Array[String]) {

    log.info("Loading application config...")

    val conf = ConfigFactory.load

    log.info("Parsing applications arguments")

    val sparkConf = ConfArguments.parse(args.toList, new SparkConf()
      .setMaster("local[2]")
      .setAppName("twitter-stream-ml-linear-regression"))

    log.info("Initializing session stats...")

    val session = new SessionStats(
      sparkConf.get("spark.app.name"),
      conf.getString("lightning"),
      conf.getString("twtweb")
    ).open

    log.info("Initializing Spark Machine Learning Model...")

    val model = new StreamingLinearRegressionWithSGD()
      .setNumIterations(Utils.numIterations)
      .setStepSize(Utils.stepSize)
      .setMiniBatchFraction(Utils.miniBatchFraction)
      .setInitialWeights(Vectors.zeros(Utils.numFeatures))

    log.info("Initializing Streaming Spark Context...")

    val ssc = new StreamingContext(sparkConf, Utils.timing)

    log.info("Initializing Twitter stream...")

    val stream = TwitterUtils.createStream(ssc, None)
      .filter(Utils.filtrate)
      .map(Utils.featurize)
      .cache()

    log.info("Initializing prediction model...")

    var count = 0L

    stream.foreachRDD({ rdd =>
      if (rdd.isEmpty) log.debug("batch: 0")
      else {
        val realPred = rdd.map{ lb =>
          (lb.label, Utils.round(model.latestModel.predict(lb.features)))
        }
        val batch = rdd.count
        count += batch
        val real = realPred.map(_._1)
        val pred = realPred.map(_._2)
        val realStdev = Utils.round(real.stdev)
        val predStdev = Utils.round(pred.stdev)
        val mse = Utils.round(realPred.map{case(v, p) => math.pow((v - p), 2)}.mean())

        if (log.isDebugEnabled) {
          log.debug("count: {}", count)
          // batch, mse (training mean squared error)
          log.debug("batch: {},  mse: {}", batch, mse)
          log.debug("stdev (real, pred): ({}, {})", realStdev.toLong,
            predStdev.toLong)
          log.debug("value (real, pred): {} ...", realPred.take(10).toArray)
        }

        session.update(count, batch, mse, realStdev, predStdev,
          real.toArray, pred.toArray);

      }

    })

    log.info("Initializing training model...")

    // training after prediction
    model.trainOn(stream)

    // Start the streaming computation
    ssc.start()
    log.info("Initialization complete.")
    ssc.awaitTermination()
  }

}
