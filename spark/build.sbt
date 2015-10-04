import Process._

name := "twtml-spark"

// scala specific version to run apache spark default package
scalaVersion := "2.10.4"

// add provided dependencies to run classpath
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

mainClass := Some("com.giorgioinf.twtml.spark.LinearRegression")
mainClass in (Compile, run) := Some("com.giorgioinf.twtml.spark.LinearRegression")

// spark package extras
spName := "giorgioinf/twitter-stream-ml"
sparkVersion := "1.4.1"
spIgnoreProvided := true
//spAppendScalaVersion := true

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
    "org.apache.spark" %% "spark-mllib" % "1.4.1" % "provided",
    "org.apache.spark" %% "spark-streaming" % "1.4.1" % "provided",
    ("org.apache.spark" %% "spark-streaming-twitter" % "1.4.1")
      .exclude("org.spark-project.spark", "unused"),
    "org.twitter4j" % "twitter4j-stream" % "3.0.3",
    "org.scalaj" %% "scalaj-http" % "1.1.4",
    "org.json4s" %% "json4s-native" % "3.2.9",
    "org.lightning-viz" %% "lightning-scala" % "0.1.0-SNAPSHOT" from "file://lib/lightning-scala_2.10-0.1.0-SNAPSHOT"
)
