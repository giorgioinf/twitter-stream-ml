package com.giorgioinf.twtml.spark

import org.apache.spark.SparkConf

object ConfArguments {

  val usage = """
Usage: sbt spark/run
Usage: sbt "spark/run [options]"

  Options:
  --master <master_url>                    spark://host:port, mesos://host:port, yarn, or local.
  --name <name>                            A name of your application.
  --consumerKey <consumerKey>              Twitter's consumer key
  --consumerSecret <consumerSecret>        Twitter's consumer secret
  --accessToken <accessToken>              Twitter's access token
  --accessTokenSecret <accessTokenSecret>  Twitter's access token secret
  """

  def parse(list: List[String], conf: SparkConf = new SparkConf) : SparkConf = {
    list match {
      case Nil => conf
      case "--master" :: value :: tail => {
        conf.setMaster(value)
        parse(tail, conf)
      }
      case "--name" :: value :: tail => {
        conf.setAppName(value)
        parse(tail, conf)
      }
      case "--consumerKey" :: value :: tail => {
        System.setProperty("twitter4j.oauth.consumerKey", value)
        parse(tail, conf)
      }
      case "--consumerSecret" :: value :: tail => {
        System.setProperty("twitter4j.oauth.consumerSecret", value)
        parse(tail, conf)
      }
      case "--accessToken" :: value :: tail => {
        System.setProperty("twitter4j.oauth.accessToken", value)
        parse(tail, conf)
      }
      case "--accessTokenSecret" :: value :: tail => {
        System.setProperty("twitter4j.oauth.accessTokenSecret", value)
        parse(tail, conf)
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