package com.giorgioinf.twtml.web

import akka.actor.{Actor,ActorSystem,Props}
import com.typesafe.config.ConfigFactory
import org.mashupbots.socko.events.{HttpRequestEvent,HttpResponseStatus}
import org.mashupbots.socko.handlers.{StaticContentHandler,StaticContentHandlerConfig,StaticResourceRequest}
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.routes.{GET,HttpRequest,Path,PathSegments,POST,Routes,WebSocketHandshake,WebSocketFrame}
import org.mashupbots.socko.webserver.{WebServer,WebServerConfig}
import util.Properties

object Server extends Logger {

  val actorSystem = ActorSystem("twtml-web", ConfigFactory.load)

  val staticRouter = actorSystem.actorOf(Props(new StaticContentHandler(StaticContentHandlerConfig())))

  def onWebSocketHandshakeComplete(webSocketId: String) = {
    actorSystem.actorOf(Props[ApiHandler]) ! WsStartHandler(webSocketId)
  }

  val routes = Routes({
        // api websocktes routes
        case WebSocketHandshake(wsHandshake) => wsHandshake match {
          case Path("/api") => {
            wsHandshake.authorize(
              onComplete = Some(onWebSocketHandshakeComplete)
            )
          }
        }
        case WebSocketFrame(wsFrame) => {
          actorSystem.actorOf(Props[ApiHandler]) ! WsFrameHandler(wsFrame)
        }

        // http routes
        case HttpRequest(httpRequest) => httpRequest match {

            // http api routes
            //
            case POST(Path("/api")) => {
              actorSystem.actorOf(Props[ApiHandler]) ! PostHandler(httpRequest)
            }

            case GET(Path("/api/config")) => {
              actorSystem.actorOf(Props[ApiHandler]) ! GetConfigHandler(httpRequest)
            }

            case GET(Path("/api/stats")) => {
              actorSystem.actorOf(Props[ApiHandler]) ! GetStatsHandler(httpRequest)
            }

            // htp page routes

            case Path("/") => {
              staticRouter ! new StaticResourceRequest(httpRequest, "public/index.html")
            }
            case PathSegments(relativePath) => {
              staticRouter ! new StaticResourceRequest(httpRequest, "public/" + relativePath.mkString("/") )
            }
            case _ => {
              httpRequest.response.write(HttpResponseStatus.NOT_FOUND)
            }
          }
        })

  val port = Properties.envOrElse("PORT", "8888").toInt // for Heroku compatibility

  val web = new WebServer(WebServerConfig(port=port, hostname="0.0.0.0"), routes, actorSystem)

  def start(): Unit = {
    web.start
    log.info("Open your browser and navigate to http://{}:{}", web.config.hostname, web.config.port )
  }

  def stop(): Unit = {
    web.webSocketConnections.closeAll()
    web.stop
  }
}
