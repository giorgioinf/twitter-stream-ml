package com.giorgioinf.twtml.spark

import org.scalatest.FunSuite
import com.typesafe.config.ConfigFactory

class ConfArgumentsSuite extends FunSuite {

  val lightningDef = "http://localhost:3000"
  val twtwebDef = "http://localhost:8888"

  val consumerKeyApp = "xxxxxxxxxxxxxxxxxxxxxxxxx"
  val consumerSecretApp = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  val accessTokenApp = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  val accessTokenSecretApp = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"

  val master = "local[4]"
  val name = "twtml-spark-test"

  val lightning = "http://lightninghost"
  val twtweb = "http://twtwebhost"
  val seconds = 123
  val stepSize = 0.01234
  val numIterations = 123
  val miniBatchFraction = 1.23
  val numRetweetBegin = 1234
  val numRetweetEnd = 12345678
  val numTextFeatures = 123456

  val consumerKey = "1234567"
  val consumerSecret = "12345678"
  val accessToken = "123456789"
  val accessTokenSecret = "1234567890"

  val ref = ConfigFactory.load("reference")

  def twt(key:String):String = {
    System.getProperty("twitter4j.oauth." + key)
  }

  test("Test config initialization - reference.conf") {

    val conf = new ConfArguments().setAppName(name)

    assert(conf.appName == name)
    assert(conf.lightning == lightningDef)
    assert(ref.getString("lightning") == lightningDef)
    assert(conf.twtweb == twtwebDef)
    assert(ref.getString("twtweb") == twtwebDef)
  }

  test("Test config twitter - application.conf") {
    val conf = new ConfArguments()

    assert(twt("consumerKey") == consumerKeyApp)
    assert(twt("consumerSecret") == consumerSecretApp)
    assert(twt("accessToken") == accessTokenApp)
    assert(twt("accessTokenSecret") == accessTokenSecretApp)
  }

  test("Test config reference.conf") {
    val conf = new ConfArguments()

    assert(conf.seconds == ref.getInt("seconds"))
    assert(conf.stepSize == ref.getDouble("stepSize"))
    assert(conf.numIterations == ref.getInt("numIterations"))
    assert(conf.miniBatchFraction == ref.getDouble("miniBatchFraction"))
    assert(conf.numRetweetBegin == ref.getInt("numRetweetBegin"))
    assert(conf.numRetweetEnd == ref.getInt("numRetweetEnd"))
    assert(conf.numTextFeatures == ref.getInt("numTextFeatures"))
  }

  test("Test config --arguments") {

    val conf = new ConfArguments().parse(List(
      "--master", master,
      "--lightning", lightning,
      "--twtweb", twtweb,
      "--seconds", seconds.toString,
      "--stepSize", stepSize.toString,
      "--numIterations", numIterations.toString,
      "--miniBatchFraction", miniBatchFraction.toString,
      "--numRetweetBegin", numRetweetBegin.toString,
      "--numRetweetEnd", numRetweetEnd.toString,
      "--numTextFeatures", numTextFeatures.toString,
      "--consumerKey", consumerKey,
      "--consumerSecret", consumerSecret,
      "--accessToken", accessToken,
      "--accessTokenSecret", accessTokenSecret
    ))

    assert(conf.master == master)
    assert(conf.lightning == lightning)
    assert(conf.twtweb == twtweb)
    assert(conf.seconds == seconds)
    assert(conf.stepSize == stepSize)
    assert(conf.numIterations == numIterations)
    assert(conf.miniBatchFraction == miniBatchFraction)
    assert(conf.numRetweetBegin == numRetweetBegin)
    assert(conf.numRetweetEnd == numRetweetEnd)
    assert(conf.numTextFeatures == numTextFeatures)
    assert(twt("consumerKey") == consumerKey)
    assert(twt("consumerSecret") == consumerSecret)
    assert(twt("accessToken") == accessToken)
    assert(twt("accessTokenSecret") == accessTokenSecret)
  }

  test("Test config -small arguments") {

    val conf = new ConfArguments().parse(List(
      "-m", master,
      "-n", name,
      "-l", lightning,
      "-w", twtweb,
      "-s", seconds.toString,
      "-p", stepSize.toString,
      "-i", numIterations.toString,
      "-b", miniBatchFraction.toString,
      "-B", numRetweetBegin.toString,
      "-E", numRetweetEnd.toString,
      "-f", numTextFeatures.toString,
      "-C", consumerKey,
      "-S", consumerSecret,
      "-A", accessToken,
      "-T", accessTokenSecret
    ))

    assert(conf.master == master)
    assert(conf.lightning == lightning)
    assert(conf.twtweb == twtweb)
    assert(conf.seconds == seconds)
    assert(conf.stepSize == stepSize)
    assert(conf.numIterations == numIterations)
    assert(conf.miniBatchFraction == miniBatchFraction)
    assert(conf.numRetweetBegin == numRetweetBegin)
    assert(conf.numRetweetEnd == numRetweetEnd)
    assert(conf.numTextFeatures == numTextFeatures)
    assert(twt("consumerKey") == consumerKey)
    assert(twt("consumerSecret") == consumerSecret)
    assert(twt("accessToken") == accessToken)
    assert(twt("accessTokenSecret") == accessTokenSecret)
  }

}