package com.giorgioinf.twtml.web

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write,read}
import scala.reflect.Manifest
import scalaj.http.{Http,HttpRequest}

class WebClient (val server:String) {

    implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Config], classOf[Stats])))

    def this() = this("http://localhost:8888")

    private def request(kind:String = ""):HttpRequest = {
      Http(server + "/api" + kind)
        .header("content-type", "application/json")
        .header("accept", "application/json")
    }

    private def post(data:TypeData) {
      val json = write(data)
      request().postData(json).asString
    }

    private def get[A:Manifest](kind:String):A = {
      val json = request(kind).asString.body
      read[A](json)
    }

    def config(id:Int, host:String, viz:List[Int]) = {
      post(Config(id, host, viz))
    }

    def stats(count:Long, batch:Long, mse:Long,
        realStddev:Long, predStddev:Long) = {
      post(Stats(count, batch, mse, realStddev, predStddev))
    }

    def config():Config = {
      get[Config]("/config")
    }

    def stats():Stats = {
      get[Stats]("/stats")
    }
}

object WebClient {
    def apply(host: String = ""): WebClient = {
    host match {
      case "" => new WebClient()
      case _ => new WebClient(host)
    }
  }
}