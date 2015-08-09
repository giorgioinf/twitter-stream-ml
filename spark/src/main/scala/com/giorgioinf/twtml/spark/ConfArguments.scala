package com.giorgioinf.twtml.spark

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf

class ConfArguments() {

  val usage = """
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
  --lightning <lightning_url>              http://localhost:3000
  --twtweb <twtweb_url>                    http://localhost:8888
  --seconds <seconds>                      5
  """

  val conf = ConfigFactory.load
  val sparkConf: SparkConf = new SparkConf

  var lightning: String = conf.getString("lightning")
  var twtweb: String = conf.getString("twtweb")
  var seconds:Int = conf.getInt("seconds")

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
      case "--help" :: tail => printUsage(0)
      case _ => printUsage(1)
    }
  }

  def printUsage(exitNumber:Int) = {
    println(usage)
    exit(exitNumber)
  }
}