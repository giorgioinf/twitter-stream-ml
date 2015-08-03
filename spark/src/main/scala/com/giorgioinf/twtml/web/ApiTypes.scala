package com.giorgioinf.twtml.web

trait TypeData

case class Config (
  id:Int = 0,
  host:String = "",
  viz:List[Int] = List[Int]()
) extends TypeData

case class Stats (
  count:Long = 0,
  batch:Long = 0,
  mse:Long = 0,
  realStddev:Long = 0,
  predStddev:Long = 0
) extends TypeData