package com.giorgioinf.twtml.spark

import org.apache.spark.{Logging, SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.StreamingLinearRegressionWithSGD
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils

object LinearRegression extends Logging {

  def main(args: Array[String]) {

    log.info("Parsing applications arguments")

    val conf = new ConfArguments()
      .setAppName("twitter-stream-ml-linear-regression")
      .parse(args.toList)

    log.info("Initializing session stats...")

    val session = new SessionStats(conf).open

    log.info("Initializing Spark Machine Learning Model...")

    MllibHelper.reset(conf)

    val model = new StreamingLinearRegressionWithSGD()
      .setNumIterations(conf.numIterations)
      .setStepSize(conf.stepSize)
      .setMiniBatchFraction(conf.miniBatchFraction)
      .setInitialWeights(Vectors.zeros(MllibHelper.numFeatures))

    log.info("Initializing Spark Context...")

    val sc = new SparkContext(conf.sparkConf)

    log.info("Initializing Streaming Spark Context... {} sec/batch", conf.seconds)

    val ssc = new StreamingContext(sc, Seconds(conf.seconds))

    log.info("Initializing Twitter stream...")

    val stream = TwitterUtils.createStream(ssc, None)
      .filter(MllibHelper.filtrate)
      .map(MllibHelper.featurize)
      .cache()

    log.info("Initializing prediction model...")

    val count = sc.accumulator(0L, "count")

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

        session.update(count.value, batch, mse, realStdev, predStdev,
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
