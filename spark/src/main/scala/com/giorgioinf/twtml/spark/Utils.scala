package com.giorgioinf.twtml.spark

object Utils {
  def round(number:Double):Double = {
    BigDecimal(number).setScale(0, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
}