package com.giorgioinf.twtml.web

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.selenium.HtmlUnit

class WebTestSuite extends FunSuite with HtmlUnit with BeforeAndAfterAll {

  val host = "http://localhost:8888"
  val configTest = Config(100, host, List(101,102))
  val statsTest = Stats(1000, 10, 2000, 15, 25)
  val client = WebClient(host)

  override def beforeAll() {

    // disable htmlunit logging
    java.util.logging.Logger.getLogger("com.gargoylesoftware")
      .setLevel(java.util.logging.Level.OFF)

    // starting webserver
    Main.main(Array("-nocache")) // ugly but works
  }

  test("The client should post the config package") {
    client.config(configTest.id, configTest.host, configTest.viz)
  }

  test("The client should get the correct config package") {
    val config = client.config
    assert(config == configTest)
  }

  test("The client should post the stats package") {
    client.stats(statsTest.count, statsTest.batch, statsTest.mse,
      statsTest.realStddev, statsTest.predStddev)
  }

  test("The client should get the correct stats package") {
    val stats = client.stats
    assert(stats == statsTest)
  }

  test("The index page should have the correct title") {
    go to (host)
    assert(pageTitle == "Twitter Stream ML")
  }

  test("The test page should have the correct title") {
   go to (host + "/test.html")
   assert(pageTitle == "Test API")
 }
}