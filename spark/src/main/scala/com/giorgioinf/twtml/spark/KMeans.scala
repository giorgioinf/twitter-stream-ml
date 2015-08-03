package com.giorgioinf.twtml.spark

import com.giorgioinf.twtml.web.WebClient
import com.typesafe.config.ConfigFactory
import org.apache.spark.{Logging, SparkConf}
import org.apache.spark.mllib.clustering.StreamingKMeans
import org.apache.spark.mllib.feature.{HashingTF,StandardScaler}
import org.apache.spark.mllib.linalg.{Vector,Vectors}
import org.apache.spark.streaming.{Seconds,StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.viz.lightning.Lightning
import scala.util.{Random, Try}
import twitter4j.Status

object KMeans extends Logging {

    val htf = new HashingTF(100)

    def featurize(status: Status): Vector = {

        val s = status.getRetweetedStatus

        val v = Vectors.dense(
            //s.getRetweetCount.toDouble/100000L,
            //s.getUser.getFollowersCount.toDouble/10000000L
            s.getRetweetCount.toDouble,
            s.getUser.getFollowersCount.toDouble
        )
        v
        //normalizer.transform(v)
        //s.getURLEntities.length.toDouble,
        //s.getUserMentionEntities.length.toDouble,

        //val v = htf.transform(s.getText.split("\\s+")).asInstanceOf[SparseVector]
        /*// sum 4 values to HashingTF vector
        val n:Int = v.size
        val indExt:Array[Int] = Array(n, n+1, n+2, n+3)

        val indices:Array[Int] = v.indices ++ indExt
        val values:Array[Double] = v.values ++ valExt


        Vectors.sparse(n+4, indices, values)*/

        //LabeledPoint(status.getRetweetCount.toDouble, v)

    }

    def main(args: Array[String]) {

        log.info("Loading application config...")

        val conf = ConfigFactory.load
        lazy val lgnHost = conf.getString("lightning")
        lazy val webHost = conf.getString("twtweb")
        lazy val web = WebClient(webHost)
        val numDimensions = 2
        val numClusters = 3

        log.info("Initializing Streaming Spark Context...")

        val sparkConf = new SparkConf()
            .setAppName("twitter-stream-ml-kmeans")

        val ssc = new StreamingContext(sparkConf, Seconds(5))

        log.info("Initializing Twitter stream...")

        val model = new StreamingKMeans()
          .setK(numClusters)
          //.setDecayFactor(1.0)
          .setHalfLife(5, "batches")
          .setRandomCenters(numDimensions, 0.0)

        val stream = TwitterUtils.createStream(ssc, None)

        val data = stream.filter(s => (s.getLang=="en" && s.isRetweet)).map(featurize)

        //data.print()

        log.info("Initializing Lightning graph session...")

        val lgn = Lightning(lgnHost)
        lgn.createSession("twitter-stream-ml-kmeans")

        val scatter = lgn.scatterstreaming(Array(0.0), Array(0.0), size=Array(0.1))

        val black = Array.fill(numClusters, 3)(0)

        //val line = lgn.linestreaming(Array(Array(0.0), Array(0.0), Array(0.0)), size=size)
        //val line = lgn.linestreaming(Array.fill(numClusters , 1)(0.0), size=Array.fill(numClusters)(5.0))

        Try(web.config(lgn.session, lgnHost, List(scatter.id)))

        var count:Long = 0

        data.foreachRDD { rdd =>
            if (rdd.count > 0) {
                count += rdd.count
                val scaledData = new StandardScaler(false, true).fit(rdd).transform(rdd)

                model.latestModel.update(scaledData, model.decayFactor, model.timeUnit)

                val datax = scaledData.map(_.apply(0)).toArray
                val datay = scaledData.map(_.apply(1)).toArray
                val centers = model.latestModel.clusterCenters
                val modelx = centers.map(_.apply(0))
                val modely = centers.map(_.apply(1))
                val modelline = centers.map(p => Array(p.apply(0)))
                val pred = model.latestModel.predict(scaledData).toArray

                //Try(line.append(modelline))
                //Try(web.stats(count))

                if (log.isDebugEnabled) {
                  log.debug(
                    "\n\tmodelx: " + modelx.deep +
                    "\n\tmodely: " + modely.deep +
                    "\n\tdatax: " + datax.deep +
                    "\n\tdatay: " + datay.deep +
                    "\n\tpred: " + pred.deep +
                    "\n\tmodelline: " + modelline.deep
                  )
                }

                Try(scatter.append(datax, datay, label=pred))
                Try(scatter.append(modelx, modely
                  //, color=black
                  ))

            }
        }


        //model.trainOn(data)

        //val predictions = model.predictOn(data)

        //val predictions = model.predictOn(data)

        // //predictions.print()

        // predictions.foreachRDD { rdd =>
        //     val centers = model.latestModel.clusterCenters
        //     val weights = model.latestModel.clusterWeights
        //     val modelx = centers.map(_.apply(0))
        //     val modely = centers.map(_.apply(1))

        //     val predn = rdd.count
        //     val pred = rdd.toArray

        //     println("modelx: " + modelx.deep)
        //     println("modely: " + modely.deep)
        //     println("predn: " + predn)
        //     println("pred: " + pred.deep)
        //     println("clusterWeights: " + weights.deep)
        //     println()

        // }

        // Start the streaming computation
        log.info("Initialization complete.")
        ssc.start()
        ssc.awaitTermination()

    }
}