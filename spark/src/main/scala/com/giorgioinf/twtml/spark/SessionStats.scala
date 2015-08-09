package com.giorgioinf.twtml.spark

import com.giorgioinf.twtml.web.WebClient
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.viz.lightning.Lightning
import org.viz.lightning.types.VisualLineStreaming
import scala.util.Try

class SessionStats(conf:ConfArguments) extends Logging {

  def lgn = Lightning(conf.lightning)
  def web = WebClient(conf.twtweb)
  var graph:VisualLineStreaming = _

  // blue
  val realColorDet = Array(173,216,230)
  val realColor = Array(30,144,255)
  // yellow
  val predColorDet = Array(238,232,170)
  val predColor = Array(255,215,0)

  def update(count:Long, batch:Long, mse:Double,
      realStdev:Double, predStdev:Double,
      real:Array[Double], pred:Array[Double]) {

    val realStdevArr = Array.fill(batch.toInt)(realStdev)
    val predStdevArr = Array.fill(batch.toInt)(predStdev)

    Try(web.stats(count, batch, mse.toLong, realStdev.toLong, predStdev.toLong))

    Try(graph.append(series = Array(real, pred, realStdevArr, predStdevArr)))
  }

  def open():this.type = {

    log.info("Initializing plot on lightning server: {}", conf.lightning)

    // create lightning session

    //lgn.createSession(conf.getAppName)

    // plot new graph
    graph = lgn.linestreaming(
        series = Array.fill(4)(Array(0.0)),
        size = Array(2, 2, 4, 4),
        color = Array(realColorDet, predColorDet,realColor, predColor)
      )

    log.info("Initializing config on we server: {}", conf.twtweb)

    // send config to web server
    Try(web.config(lgn.session, lgn.host, List(graph.id)))
    this
  }
}