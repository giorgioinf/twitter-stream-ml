package com.giorgioinf.twtml.web

import org.mashupbots.socko.events.{HttpRequestEvent,WebSocketFrameEvent}
import org.mashupbots.socko.infrastructure.Logger
import akka.actor.Actor

import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

// command events

case class PostHandler(event: HttpRequestEvent)
case class GetConfigHandler(event: HttpRequestEvent)
case class GetStatsHandler(event: HttpRequestEvent)
case class WsFrameHandler(event: WebSocketFrameEvent)
case class WsStartHandler(webSocketId: String)
//case class HomePage(event: HttpRequestEvent)
//case class ShowQueryStringDataPage(event: HttpRequestEvent)
//case class ShowPostDataPage(event: HttpRequestEvent)

/**
 * Hello processor writes a greeting and stops.
 */
 class ApiHandler extends Logger with Actor {

  implicit val formats = DefaultFormats

  val ok = write(("status" -> "OK"))

  def response(event:HttpRequestEvent, json:String) = {
    event.response.contentType = "application/json"
    event.response.write(json)
    context.stop(self)
  }

  def receive = {

    case GetConfigHandler(event) => {
      val json = ApiCache.config
      log.debug("http - get config {}", json)
      response(event, json)
    }

    case GetStatsHandler(event) => {
      val json = ApiCache.stats
      log.debug("http - get stats {}", json)
      response(event, json)
    }

    case PostHandler(event) => {
      val json = event.request.content.toString()
      log.debug("http - post data {}", json)
      ApiCache.cache(json)
      response(event, ok)
      log.debug("websocket - send all data {}", json)
      Server.web.webSocketConnections.writeText(json)
    }

    case WsFrameHandler(event) => {
      val json = event.readText
      log.debug("websocket - {} - read data {}", Array(event.webSocketId, json))
      ApiCache.cache(json)

      log.debug("websocket - send all data {}", json)
      Server.web.webSocketConnections.writeText(json)
      context.stop(self)
    }
    case WsStartHandler(webSocketId) => {
      val json = ApiCache.config
      log.debug("websocket - {} - connected, get config {}", Array(webSocketId, json))
      Server.web.webSocketConnections.writeText(json, webSocketId)
      context.stop(self)
    }
  }
}