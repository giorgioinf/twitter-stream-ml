resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

name := "root"

scalaVersion := "2.11.6"

run := {}

publish := {}

publishLocal := {}

publishArtifact in (Compile, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.11.6",
  scalacOptions := Seq("-encoding", "utf8"),
  homepage := Some(url("https://github.com/giorgioinf/twitter-stream-ml")),
  description := "Machine learning over twitter stream with apache spark.",
  organization := "com.giorgioinf",
  organizationName := "giorgioinf.com",
  organizationHomepage := Some(url("http://giorgioinf.com")),
  assemblyJarName in assembly := s"${name.value}_${scalaVersion.value.dropRight(2)}-${version.value}.jar",
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  }
)

lazy val web = project.settings(commonSettings)

lazy val spark = project.settings(commonSettings)
