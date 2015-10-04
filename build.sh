#!/bin/sh
# temporary distribution method
# The spDist don't recognize local jars
# This method uses assembled jar unless spDist jar
# The zip contains the web application too

version="0.1.1"

sbt clean assembly
sbt spark/spMakePom
mv spark/target/scala-2.10/twtml-spark_*.pom target/twitter-stream-ml-${version}.pom
mv spark/target/scala-2.10/twtml-spark_*.jar target/twitter-stream-ml-${version}.jar
mv web/target/scala-2.11/twtml-web_*.jar target/
cd target
zip twitter-stream-ml-${version}.zip twitter-stream-ml-*.pom \
twitter-stream-ml-*.jar \
twtml-web_*.jar
cd ..
