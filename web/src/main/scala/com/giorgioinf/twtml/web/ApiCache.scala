package com.giorgioinf.twtml.web

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write,read}
import org.mashupbots.socko.infrastructure.Logger
import scala.io.Source
import scala.tools.nsc.io.File
import scala.util.{Properties,Try}

object ApiCache extends Logger {

  private val backupFile = Properties.tmpDir + "/twtml-web.json"

  private var typeStats = Stats()

  private var typeConfig = Config()

  implicit val formats = Serialization.formats(
    ShortTypeHints(List(classOf[Config], classOf[Stats])))

  private def cacheStats(data:Stats) = {
    log.debug("caching stats")
    typeStats = data
  }

  private def cacheConfig(data:Config) = {
    log.debug("caching config")
    typeConfig = data
    backup
  }

  def config():String = {
    write(typeConfig)
  }

  def stats():String = {
    write(typeStats)
  }

  def cache(json:String) = {
    val data = read[TypeData](json)
    data match {
      case stat:Stats => cacheStats(stat)
      case conf:Config => cacheConfig(conf)
      case _ => log.error("json not recognized: {}", json)
    }
  }

  def restore() = {
    Try(cache(Source.fromFile(backupFile).mkString))
  }

  def backup() = {
    File(backupFile).writeAll(config)
  }
}