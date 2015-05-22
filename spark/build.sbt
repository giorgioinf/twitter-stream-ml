import Process._

name := "twtml-spark"

// scala specific version to run apache spark default package
scalaVersion := "2.10.5"

// get run command
run := (run in Compile).evaluated

// run command depends of assembly to generate a spark job package
run <<= run dependsOn (assembly in Compile)

//mainClass := Some("com.giorgioinf.twtml.spark.KMeans")
mainClass := Some("com.giorgioinf.twtml.spark.LinearRegression")

// run command have another run class, to execute spark-submit with assembled package
mainClass in (Compile, run) := Some("com.giorgioinf.twtml.spark.Run")

// extra jars stored on lib folder
unmanagedBase <<= baseDirectory {base => base/"lib"}

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.3.1" % "provided",
    "org.apache.spark" %% "spark-mllib" % "1.3.1" % "provided",
    "org.apache.spark" %% "spark-streaming" % "1.3.1" % "provided",
    "org.apache.spark" %% "spark-streaming-twitter" % "1.3.1",
    "org.twitter4j" % "twitter4j-core" % "3.0.6"
)