enablePlugins(SbtWeb)

import NativePackagerKeys._

packageArchetype.java_application

name := "twtml-web"

mainClass := Some("com.giorgioinf.twtml.web.Main")

libraryDependencies ++= Seq(
    "org.mashupbots.socko" %% "socko-webserver" % "0.6.0",
    "org.json4s" %% "json4s-native" % "3.2.9",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.seleniumhq.selenium" % "selenium-java" % "2.45.0" % "test",
    "org.scalaj" %% "scalaj-http" % "1.1.4" % "test",
    "org.webjars" % "jquery" % "1.11.3",
    "org.webjars" % "jquery-atmosphere" % "2.2.3",
    "org.webjars" % "bootstrap" % "3.3.4"
)

// external dependency
// https://github.com/nprapps/pym.js/tree/0.4.3

// adapt SbtWeb on public package inside jar

compile in Compile <<= (compile in Compile) dependsOn WebKeys.stage

unmanagedClasspath in Runtime += WebKeys.webTarget.value / "stage"

unmanagedClasspath in Test += WebKeys.webTarget.value / "stage"

unmanagedResourceDirectories in Compile += WebKeys.webTarget.value / "stage"

WebKeys.stagingDirectory := WebKeys.webTarget.value / "stage/public"

WebKeys.packagePrefix in Assets := "public/"

pipelineStages := Seq(uglify)


