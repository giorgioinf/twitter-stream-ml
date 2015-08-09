package com.giorgioinf.twtml.spark

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf

object Main {

  def main(args: Array[String]) {

    val conf = ConfigFactory.load

    val sparkConf = ConfArguments.parse(args.toList, new SparkConf()
      .setMaster("local[2]")
      .setAppName("twitter-stream-ml"))

    println(sparkConf.toDebugString)

    val session = new SessionStats(
      sparkConf.get("spark.app.name"),
      conf.getString("lightning"),
      conf.getString("twtweb")
    ).open()

  }

}