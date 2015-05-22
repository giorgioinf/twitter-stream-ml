resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")

// heroku
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.4")

// web plugins
addSbtPlugin("com.typesafe.sbt" %% "sbt-web" % "1.2.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")