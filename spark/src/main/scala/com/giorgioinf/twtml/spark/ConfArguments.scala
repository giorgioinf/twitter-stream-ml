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
  -h, --help
  -m, --master <master_url>                    spark://host:port, mesos://host:port, yarn, or local.
  -n, --name <name>                            A name of your application.
  -C, --consumerKey <consumerKey>              Twitter's consumer key
  -S, --consumerSecret <consumerSecret>        Twitter's consumer secret
  -A, --accessToken <accessToken>              Twitter's access token
  -T, --accessTokenSecret <accessTokenSecret>  Twitter's access token secret
  -l, --lightning <lightning_url>              $lightningDef
  -w, --twtweb <twtweb_url>                    $twtwebDef
  -s, --seconds <integer number>               Default: $secondsDef
  -p, --stepSize <float number>                Default: $stepSizeDef
  -i, --numIterations <integer number>         Default: $numIterationsDef
  -b, --miniBatchFraction <float number>       Default: $miniBatchFractionDef
  -B, --numRetweetBegin <integer number>       Default: $numRetweetBeginDef
  -E, --numRetweetEnd <integer number>         Default: $numRetweetEndDef
  -f, --numTextFeatures <integer number>       Default: $numTextFeaturesDef
  """

  if (System.getProperty("SPARK_SUBMIT") != "true") {
      sparkConf.setMaster("local[*]")
  }

  if (conf.getString("consumerKey") != "") {
      System.setProperty("twitter4j.oauth.consumerKey",
        conf.getString("consumerKey"))
  }

  if (conf.getString("consumerSecret") != "") {
      System.setProperty("twitter4j.oauth.consumerSecret",
        conf.getString("consumerSecret"))
  }

  if (conf.getString("accessToken") != "") {
      System.setProperty("twitter4j.oauth.accessToken",
        conf.getString("accessToken"))
  }

  if (conf.getString("accessTokenSecret") != "") {
      System.setProperty("twitter4j.oauth.accessTokenSecret",
        conf.getString("accessTokenSecret"))
  }

  def appName(): String = {
    sparkConf.get("spark.app.name")
  }

  def setAppName(appName: String): this.type = {
    sparkConf.setAppName(appName)
    this
  }

  def master():String = {
    sparkConf.get("spark.master")
  }

  def parse(list: List[String]) : this.type = {

    list match {
      case Nil => this
      case ("--master" | "-m") :: value :: tail => {
        sparkConf.setMaster(value)
        parse(tail)
      }
      case ("--name" | "-n") :: value :: tail => {
        sparkConf.setAppName(value)
        parse(tail)
      }
      case ("--consumerKey" | "-C" ) :: value :: tail => {
        System.setProperty("twitter4j.oauth.consumerKey", value)
        parse(tail)
      }
      case ("--consumerSecret" | "-S" ) :: value :: tail => {
        System.setProperty("twitter4j.oauth.consumerSecret", value)
        parse(tail)
      }
      case ("--accessToken" | "-A" ) :: value :: tail => {
        System.setProperty("twitter4j.oauth.accessToken", value)
        parse(tail)
      }
      case ("--accessTokenSecret" | "-T" ) :: value :: tail => {
        System.setProperty("twitter4j.oauth.accessTokenSecret", value)
        parse(tail)
      }
      case ("--lightning" | "-l") :: value :: tail => {
        lightning = value
        parse(tail)
      }
      case ("--twtweb" | "-w") :: value :: tail => {
        twtweb = value
        parse(tail)
      }
      case ("--seconds" | "-s" ) :: value :: tail => {
        seconds = value.toInt
        parse(tail)
      }
      case ("--stepSize" | "-p" ) :: value :: tail => {
        stepSize = value.toDouble
        parse(tail)
      }
      case ("--numIterations" | "-i") :: value :: tail => {
        numIterations = value.toInt
        parse(tail)
      }
      case ("--miniBatchFraction" | "-b") :: value :: tail => {
        miniBatchFraction = value.toDouble
        parse(tail)
      }
      case ("--numRetweetBegin" | "-B") :: value :: tail => {
        numRetweetBegin = value.toInt
        parse(tail)
      }
      case ("--numRetweetEnd" | "-E") :: value :: tail => {
        numRetweetEnd = value.toInt
        parse(tail)
      }
      case ("--numTextFeatures" | "-f") :: value :: tail => {
        numTextFeatures = value.toInt
        parse(tail)
      }
      case ("--help" | "-h") :: tail => printUsage(0)
      case _ => printUsage(1)
    }
  }

  def printUsage(exitNumber:Int) = {
    println(usage)
    exit(exitNumber)
  }
}
