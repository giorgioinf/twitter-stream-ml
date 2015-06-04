import Process._

name := "twtml-spark"

// scala specific version to run apache spark default package
scalaVersion := "2.10.5"

// add provided depedencies to run classpath
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

mainClass := Some("com.giorgioinf.twtml.spark.LinearRegression")
mainClass in (Compile, run) := Some("com.giorgioinf.twtml.spark.LinearRegression")

// extra jars stored on lib folder
unmanagedBase <<= baseDirectory {base => base/"lib"}

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-mllib" % "1.3.1" % "provided",
    "org.apache.spark" %% "spark-streaming" % "1.3.1" % "provided",
    "org.apache.spark" %% "spark-streaming-twitter" % "1.3.1",
    "org.twitter4j" % "twitter4j-core" % "3.0.6"
)