resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"

// ide/debug
addSbtPlugin("org.ensime" % "ensime-sbt" % "0.4.0")

// package local
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")

// spark package tool
addSbtPlugin("org.spark-packages" % "sbt-spark-package" % "0.2.4")

// package heroku
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.4")

// web plugins
addSbtPlugin("com.typesafe.sbt" %% "sbt-web" % "1.2.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")
