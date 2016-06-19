enablePlugins(SbtWeb)

import NativePackagerKeys._

packageArchetype.java_application

name := "twtml-web"

mainClass := Some("com.giorgioinf.twtml.web.Main")

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
    "org.scalaj" %% "scalaj-http" % "2.3.0" % "test",
    "org.mashupbots.socko" %% "socko-webserver" % "0.6.0",
    "org.json4s" %% "json4s-native" % "3.2.9",
    "org.webjars" % "jquery" % "1.12.4",
    "org.webjars" % "jquery-atmosphere" % "2.2.13",
    "org.webjars" % "bootstrap" % "3.3.6"
)

// external dependency
// https://github.com/nprapps/pym.js/tree/0.4.5

// adapt SbtWeb on public package inside jar

compile in Compile <<= (compile in Compile) dependsOn WebKeys.stage

unmanagedClasspath in Runtime += WebKeys.webTarget.value / "stage"

unmanagedClasspath in Test += WebKeys.webTarget.value / "stage"

unmanagedResourceDirectories in Compile += WebKeys.webTarget.value / "stage"

WebKeys.stagingDirectory := WebKeys.webTarget.value / "stage/public"

WebKeys.packagePrefix in Assets := "public/"

pipelineStages := Seq(uglify)
