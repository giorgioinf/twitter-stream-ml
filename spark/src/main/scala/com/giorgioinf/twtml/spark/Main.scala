package com.giorgioinf.twtml.spark

import org.apache.spark.SparkConf

object Main {

  def main(args: Array[String]) {

    val conf = ConfArguments.parse(args.toList, new SparkConf()
      .setMaster("local[2]")
      .setAppName("twitter-stream-ml"))

    println(conf.toDebugString)
  }

}