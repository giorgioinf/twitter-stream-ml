package com.giorgioinf.twtml.spark

import org.apache.spark.SparkConf

object ConfArguments {

  val usage = """
Usage: sbt spark/run
Usage: sbt "spark/run [options]"

  Options:
  --master MASTER_URL         spark://host:port, mesos://host:port, yarn, or local.
  --name NAME                 A name of your application.
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
      case "--help" :: tail => printUsage(0)
      case _ => printUsage(1)
    }
  }

  def printUsage(exitNumber:Int) = {
    println(usage)
    exit(exitNumber)
  }
}