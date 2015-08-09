package com.giorgioinf.twtml.spark

import com.giorgioinf.twtml.web.WebClient
import org.apache.spark.rdd.RDD
import org.viz.lightning.Lightning
import org.viz.lightning.types.VisualLineStreaming
import scala.util.Try

class SessionStats(name:String, lightning:String, twtweb:String) {

  def lgn = Lightning(lightning)
  def web = WebClient(twtweb)
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
    // create lightning session

    //lgn.createSession(name)

    // plot new graph
    graph = lgn.linestreaming(
        series = Array.fill(4)(Array(0.0)),
        size = Array(2, 2, 4, 4),
        color = Array(realColorDet, predColorDet,realColor, predColor)
      )

    // send config to web server
    Try(web.config(lgn.session, lgn.host, List(graph.id)))
    this
  }
}