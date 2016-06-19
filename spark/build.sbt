import Process._

name := "twtml-spark"

scalaVersion := "2.10.5" // apache-spark scala version

// add provided dependencies to run classpath
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

mainClass := Some("com.giorgioinf.twtml.spark.LinearRegression")
mainClass in (Compile, run) := mainClass.value

// spark package extras
spName := "giorgioinf/twitter-stream-ml"
sparkVersion := "1.6.1"
sparkComponents ++= Seq("mllib", "streaming", "streaming-twitter")
spIgnoreProvided := true
// spAppendScalaVersion := true

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.apache.spark" %% "spark-core" % sparkVersion.value % "provided",
    "org.apache.spark" %% "spark-mllib" % sparkVersion.value% "provided",
    "org.apache.spark" %% "spark-streaming" % sparkVersion.value % "provided",
    ("org.apache.spark" %% "spark-streaming-twitter" % sparkVersion.value)
      .exclude("org.spark-project.spark", "unused"),
    "org.twitter4j" % "twitter4j-stream" % "4.0.4",
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "org.json4s" %% "json4s-native" % "3.2.9"
    // "org.lightning-viz" %% "lightning-scala" % "0.1.0-SNAPSHOT" from
    //   (baseDirectory.value / "lib" / "lightning-scala_2.10-0.1.0-SNAPSHOT.jar").toURL.toExternalForm
)
