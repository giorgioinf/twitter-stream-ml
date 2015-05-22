package com.giorgioinf.twtml.spark

import scala.collection.JavaConversions._
import sys.process._


object Run {

    def main(args: Array[String]) {
        val file = "spark/target/scala-2.10/twtml-spark_2.10-0.1.0.jar"
        val strArgs = args.mkString(" ")
        val cmd = s"spark-submit $strArgs $file"

        println(cmd)

        cmd !
    }
}