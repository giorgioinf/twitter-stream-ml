package com.giorgioinf.twtml.spark

import java.text.Normalizer
import org.apache.spark.Logging
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.{SparseVector, Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import scala.math.BigDecimal
import twitter4j.Status

object MllibHelper extends Logging {

  val numNumberFeatures = 4

  var numRetweetBegin = 100
  var numRetweetEnd = 1000
  var numTextFeatures = 1000
  var hashText = new HashingTF(numTextFeatures)
  var numFeatures = numTextFeatures + numNumberFeatures
  var numberFeatureIndices = (numTextFeatures to numFeatures-1).toArray

  def reset(conf:ConfArguments) {
    numRetweetBegin = conf.numRetweetBegin
    numRetweetEnd = conf.numRetweetEnd
    numTextFeatures = conf.numTextFeatures

    var hashText = new HashingTF(numTextFeatures)
    var numFeatures = numTextFeatures + numNumberFeatures
    var numberFeatureIndices = (numTextFeatures to numFeatures-1).toArray

    log.debug(s"retweet range: ($numRetweetBegin - $numRetweetEnd), numTextFeatures: $numTextFeatures")
  }

  /**
   * Create feature vectors by turning each tweet into bigrams of
   * characters (an n-gram model) and then hashing those to a
   * length-1000 feature vector that we can pass to MLlib.
   * This is a common way to decrease the number of features in a
   * model while still getting excellent accuracy (otherwise every
   * pair of Unicode characters would potentially be a feature).
   */
  def featurizeText(statuses: Status): SparseVector = {
    val text = statuses.getRetweetedStatus
      .getText
      .toLowerCase

    // Separate accents from characters and then remove non-unicode
    // characters
    val noAccentText = Normalizer
      .normalize(text, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")

    // bigrams
    hashText.transform(text.sliding(2).toSeq)
      .asInstanceOf[SparseVector]
  }

  def featurizeNumbers(statuses: Status): Vector = {
    val user = statuses.getRetweetedStatus.getUser
    val created = statuses.getRetweetedStatus.getCreatedAt
    val timeLeft = (System.currentTimeMillis - created.getTime)

    Vectors.dense(
      user.getFollowersCount * Math.pow(10, -12),
      user.getFavouritesCount * Math.pow(10, -12),
      user.getFriendsCount * Math.pow(10, -12),
      timeLeft * Math.pow(10, -14)
      //retweeted.getURLEntities.length,
      //retweeted.getUserMentionEntities.length
    )
  }

  def featurize(statuses: Status): LabeledPoint = {
    val textFeatures = featurizeText(statuses)
    val numberFeatures = featurizeNumbers(statuses)
    val features = Vectors.sparse(
      numFeatures,
      textFeatures.indices ++ numberFeatureIndices,
      textFeatures.values ++ numberFeatures.toArray
    )
    LabeledPoint( statuses.getRetweetedStatus.getRetweetCount.toDouble, features )
  }

  def retweetInterval(statuses: Status, start:Long, end:Long):Boolean = {
    val n = statuses.getRetweetedStatus.getRetweetCount
    (n >= start && n <= end)
  }

  def filtrate(statuses: Status): Boolean = {
    (
      statuses.isRetweet &&
      //statuses.getLang == "en" &&
      retweetInterval(statuses, numRetweetBegin, numRetweetEnd)
    )
  }
}