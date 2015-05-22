# twitter-stream-ml
Machine Learning over Twitter's stream. Using Apache Spark, Web Server and Lightning Graph server.

## Configuration
Just only spark job needs a configuration.

### Server locations

Edit spark **application.conf** to change server locations

**spark/src/main/resources/application.conf**
```ini
lightning="http://localhost:3000"
twtweb="http://localhost:8888"
```

### Twitter access token

Create spark **twitter4j.properties** to add twitter's access tokens

**spark/src/main/resources/twitter4j.properties**
```ini
oauth.consumerKey=xxxxxxxxxxxxxxxxxxxxxxxxx
oauth.consumerSecret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
oauth.accessToken=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
oauth.accessTokenSecret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## Build

```sh
$ sbt assembly
```


## Run Web Server [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/giorgioinf/twitter-stream-ml/tree/master)

```sh
$ scala web/target/scala-2.11/twtml-web_2.11-*.jar
```
or

```sh
$ sbt web/run
```


## Run Spark Job

```sh
$ spark-submit spark/target/scala-2.10/twtml-spark_2.10-*.jar --master <master>
```
or

```sh
$ sbt "spark/run --master <master>"
```


## Dependencies

### Lightning [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/lightning-viz/lightning/tree/master)

Lightning is a data-visualization server providing API-based access to reproducible, web-based, interactive visualizations

http://lightning-viz.org/

### Simple Build Tool (Scala) - 0.13.8

**sbt** is an open source build tool for Scala and Java projects, similar to Java's Maven or Ant.

http://www.scala-sbt.org/

### Apache Spark - 1.3.1

Apache Spark is an open-source cluster computing framework originally developed in the AMPLab at UC Berkeley. In contrast to Hadoop's two-stage disk-based MapReduce paradigm, Spark's in-memory primitives provide performance up to 100 times faster for certain applications. By allowing user programs to load data into a cluster's memory and query it repeatedly, Spark is well suited to machine learning algorithms.

http://spark.apache.org/


### Apache Haddop - 2.6.0

Apache Hadoop is an open-source software framework written in Java for distributed storage and distributed processing of very large data sets on computer clusters built from commodity hardware. All the modules in Hadoop are designed with a fundamental assumption that hardware failures are commonplace and thus should be automatically handled in software by the framework.

http://hadoop.apache.org/


## Guides

### Setting up Hadoop (v2) with Spark (v1) on OSX using Homebrew
http://datahugger.org/datascience/setting-up-hadoop-v2-with-spark-v1-on-osx-using-homebrew/
