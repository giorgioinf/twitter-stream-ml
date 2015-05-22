package com.giorgioinf.twtml.web

import scala.tools.scalap.Arguments

object Main {

  def main(args: Array[String]) = {
    val arguments = Arguments.Parser('-')
      .withOption("-nocache")
      .parse(args)

    if (!arguments.contains("-nocache")) {
      ApiCache.restore
    }

    val server = Server

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run { server.stop }
    })

    server.start
  }
}