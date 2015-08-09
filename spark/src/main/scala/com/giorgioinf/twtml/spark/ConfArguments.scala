package com.giorgioinf.twtml.spark

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf

class ConfArguments() {

  val conf                     = ConfigFactory.load
  val sparkConf                = new SparkConf
  val lightningDef             = conf.getString("lightning")
  val twtwebDef                = conf.getString("twtweb")
  val secondsDef               = conf.getInt("seconds")
  val stepSizeDef              = conf.getDouble("stepSize")
  val numIterationsDef         = conf.getInt("numIterations")
  val miniBatchFractionDef     = conf.getDouble("miniBatchFraction")
  val numRetweetBeginDef       = conf.getInt("numRetweetBegin")
  val numRetweetEndDef         = conf.getInt("numRetweetEnd")
  val numTextFeaturesDef       = conf.getInt("numTextFeatures")

  var lightning                = lightningDef
  var twtweb                   = twtwebDef
  var seconds                  = secondsDef
  var stepSize                 = stepSizeDef
  var numIterations            = numIterationsDef
  var miniBatchFraction        = miniBatchFractionDef
  var numRetweetBegin          = numRetweetBeginDef
  var numRetweetEnd            = numRetweetEndDef
  var numTextFeatures          = numTextFeaturesDef

  val usage = s"""
Usage: sbt spark/run
Usage: sbt "spark/run [options]"
Usage: spark-submit twtml-spark*.jar [options]

  Options:
  --master <master_url>                    spark://host:port, mesos://host:port, yarn, or local.
  --name <name>                            A name of your application.
  --consumerKey <consumerKey>              Twitter's consumer key
  --consumerSecret <consumerSecret>        Twitter's consumer secret
  --accessToken <accessToken>              Twitter's access token
  --accessTokenSecret <accessTokenSecret>  Twitter's access token secret
  --lightning <lightning_url>              $lightningDef
  --twtweb <twtweb_url>                    $twtwebDef
  --seconds <integer number>               Default: $seconds
  --stepSize <float number>                Default: $stepSize
  --numIterations <integer number>         Default: $numIterations
  --miniBatchFraction <float number>       Default: $miniBatchFraction
  --numRetweetBegin <integer number>       Default: $numRetweetBegin
  --numRetweetEnd <integer number>         Default: $numRetweetEnd
  --numTextFeatures <integer number>       Default: $numTextFeatures
  """

  if (System.getProperty("SPARK_SUBMIT") != "true") {
      sparkConf.setMaster("local[2]")
  }

  def getAppName(): String = {
    sparkConf.get("spark.app.name")
  }

  def setAppName(appName: String): this.type = {
    sparkConf.setAppName(appName)
    this
  }

  def parse(list: List[String]) : this.type = {

    list match {
      case Nil => this
      case "--master" :: value :: tail => {
        sparkConf.setMaster(value)
        parse(tail)
      }
      case "--name" :: value :: tail => {
        sparkConf.setAppName(value)
        parse(tail)
      }
      case "--consumerKey" :: value :: tail => {
        System.setProperty("twitter4j.oauth.consumerKey", value)
        parse(tail)
      }
      case "--consumerSecret" :: value :: tail => {
        System.setProperty("twitter4j.oauth.consumerSecret", value)
        parse(tail)
      }
      case "--accessToken" :: value :: tail => {
        System.setProperty("twitter4j.oauth.accessToken", value)
        parse(tail)
      }
      case "--accessTokenSecret" :: value :: tail => {
        System.setProperty("twitter4j.oauth.accessTokenSecret", value)
        parse(tail)
      }
      case "--lightning" :: value :: tail => {
        lightning = value
        parse(tail)
      }
      case "--twtweb" :: value :: tail => {
        twtweb = value
        parse(tail)
      }
      case "--seconds" :: value :: tail => {
        seconds = value.toInt
        parse(tail)
      }
      case "--stepSize" :: value :: tail => {
        stepSize = value.toDouble
        parse(tail)
      }
      case "--numIterations" :: value :: tail => {
        numIterations = value.toInt
        parse(tail)
      }
      case "--miniBatchFraction" :: value :: tail => {
        miniBatchFraction = value.toDouble
        parse(tail)
      }
      case "--numRetweetBegin" :: value :: tail => {
        numRetweetBegin = value.toInt
        parse(tail)
      }
      case "--numRetweetEnd" :: value :: tail => {
        numRetweetEnd = value.toInt
        parse(tail)
      }
      case "--numTextFeatures" :: value :: tail => {
        numTextFeatures = value.toInt
        parse(tail)
      }
      case "--help" :: tail => printUsage(0)
      case _ => printUsage(1)
    }
  }

  def printUsage(exitNumber:Int) = {
    println(usage)
    exit(exitNumber)
  }
}