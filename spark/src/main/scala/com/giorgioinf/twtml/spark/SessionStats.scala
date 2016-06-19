package com.giorgioinf.twtml.spark

import com.giorgioinf.twtml.web.WebClient
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.viz.lightning.{Lightning,Visualization}
import scala.util.Try

class SessionStats(conf:ConfArguments) extends Logging {

  def lgn = Lightning(conf.lightning)
  def web = WebClient(conf.twtweb)
  var viz:Visualization = _

  // blue
  val realColorDet = Array(173.0, 216.0, 230.0)
  val realColor = Array(30.0, 144.0, 255.0)
  // yellow
  val predColorDet = Array(238.0, 232.0, 170.0)
  val predColor = Array(255.0, 215.0, 0.0)

  def update(count:Long, batch:Long, mse:Double,
      realStdev:Double, predStdev:Double,
      real:Array[Double], pred:Array[Double]) {

    val realStdevArr = Array.fill(batch.toInt)(realStdev)
    val predStdevArr = Array.fill(batch.toInt)(predStdev)

    Try(web.stats(count, batch, mse.toLong, realStdev.toLong, predStdev.toLong))

    Try(lgn.lineStreaming(
      series = Array(real, pred, realStdevArr, predStdevArr),
      viz = viz))
  }

  def open():this.type = {

    log.info("Initializing plot on lightning server: {}", conf.lightning)

    // lgn.createSession(conf.appName)

    // if (lgn.session.nonEmpty) {
    //   log.info("lightning server session: {}/sessions/{}{}", conf.lightning, lgn.session, "")
    // } else {
    //   log.warn("lightning server session is empty")
    // }

    // plot new graph
    viz = lgn.lineStreaming(
        series = Array.fill(4)(Array(0.0)),
        size = Array(1.0, 1.0, 2.0, 2.0),
        color = Array(realColorDet, predColorDet, realColor, predColor))

    log.info("lightning server session: \n  {}/sessions/{}\n  {}/visualizations/{}/pym",
      conf.lightning, viz.lgn.session, conf.lightning, viz.id)

    log.info("Initializing config on web server: {}", conf.twtweb)

    // send config to web server
    Try(web.config(viz.lgn.session, lgn.host, List(viz.id)))
    this
  }
}
