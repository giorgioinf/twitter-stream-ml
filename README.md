# twitter-stream-ml
Machine Learning over Twitter's stream. Using Apache Spark, Web Server and Lightning Graph server.

## Dependencies

### Lightning

Lightning is a data-visualization server providing API-based access to reproducible, web-based, interactive visualizations

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy?template=https://github.com/lightning-viz/lightning/tree/master)

## Build

```sh
$ sbt assembly
```


## Run Web Server

```sh
$ scala web/target/scala-2.11/twtml-web_2.11-0.1.0.jar
```

or

```sh
$ sbt web/run
```


## Run Spark Job

```sh
$ scala spark/target/scala-2.10/twtml-spark_2.10-0.1.0.jar --master &lt;master&gt;
```

or

```sh
$ sbt "spark/run --master &lt;master&gt;"
```