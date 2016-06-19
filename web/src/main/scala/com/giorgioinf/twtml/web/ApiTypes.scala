package com.giorgioinf.twtml.web

trait TypeData

case class Config (
  id:String = "",
  host:String = "",
  viz:List[String] = List[String]()
) extends TypeData

case class Stats (
  count:Long = 0,
  batch:Long = 0,
  mse:Long = 0,
  realStddev:Long = 0,
  predStddev:Long = 0
) extends TypeData
