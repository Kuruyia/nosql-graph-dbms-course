# NoSQL Graph DBMS - Day 4 activities

## Activity #6: Wikipedia Data Analysis + NLP

Let's load the data entirely into memory:

```scala
// Load the file
val rawData = spark.sparkContext.textFile("data/wikipedia.dat")

// Parse the various fields
case class WikiPage(title: String, text: String, isRedirect: Boolean)

def parseWikiPage(pageString: String): WikiPage = {
  val titleRegex = "<title>(.*?)</title>".r
  val textRegex = "<text>(.*?)</text>".r

  val title = titleRegex.findFirstMatchIn(pageString).map(_.group(1)).getOrElse("")
  val text = textRegex.findFirstMatchIn(pageString).map(_.group(1)).getOrElse("")
  val isRedirect = text.contains("#REDIRECT")

  WikiPage(title, text, isRedirect)
}

// Convert the data into a dataframe
val wikiPageRDD = rawData.map(parseWikiPage)
val wikiPageDF = wikiPageRDD.toDF()
wikiPageDF.show(5, false)

wikiPageDF.groupBy("isRedirect")
  .count()
  .show()

import org.apache.spark.sql.functions._

val categoryPattern = "\\[\\[Category:(.*?)]]".r

val extractCategories = udf { text: String =>
  categoryPattern.findAllMatchIn(text).map(_.group(1)).toSeq
}

val wikiWithCategories = wikiPageDF
  .withColumn("categories", extractCategories(col("text")))

wikiWithCategories.select("title", "categories").show(false)

val exploded = wikiWithCategories
  .withColumn("category", explode(col("categories")))

// Count the top 50 categories
exploded.groupBy("category")
  .count()
  .orderBy(desc("count"))
  .show(50, truncate = false)
```

Result:

```
+-----------------------------------------------------------------------------+-----+
|category                                                                     |count|
+-----------------------------------------------------------------------------+-----+
|Living people                                                                |91   |
|December 2013 peer reviews                                                   |27   |
|JavaScript libraries                                                         |25   |
|Ajax (programming)                                                           |20   |
|Cross-platform software                                                      |19   |
|Scripting languages                                                          |16   |
|Web application frameworks                                                   |15   |
|JavaScript                                                                   |14   |
|Software using the MIT license                                               |14   |
|Approved Wikipedia bot requests for approval|{{#titleparts:{{PAGENAME}}|1|3}}|12   |
|.NET Framework                                                               |12   |
|Software design patterns                                                     |11   |
|Web services                                                                 |10   |
|Free software programmed in C++                                              |10   |
|Pending DYK nominations                                                      |10   |
|XML-based standards                                                          |10   |
|Lists of software                                                            |10   |
|Application programming interfaces                                           |9    |
|Computer programming tools                                                   |9    |
|Free software                                                                |8    |
|Integrated development environments                                          |8    |
|Object-oriented programming languages                                        |8    |
|Data types                                                                   |8    |
|Object-oriented programming                                                  |8    |
|Software using the Apache license                                            |8    |
|HTML                                                                         |8    |
|Markup languages                                                             |8    |
|Video game development software                                              |8    |
|Cross-platform free software                                                 |8    |
|Web development                                                              |7    |
|Functional languages                                                         |7    |
|World Wide Web Consortium standards                                          |7    |
|Gopher clients                                                               |7    |
|Free software programmed in JavaScript                                       |6    |
|Articles with example C code                                                 |6    |
|Mozilla                                                                      |6    |
|Web browsers                                                                 |6    |
|Rich Internet application frameworks                                         |6    |
|XML                                                                          |6    |
|Articles with example Java code                                              |6    |
|Programming language comparisons                                             |6    |
|Data serialization formats                                                   |6    |
|Free computer libraries                                                      |6    |
|DYK/Nominations|Pending                                                      |6    |
|Firefox add-ons                                                              |6    |
|Computer programming                                                         |6    |
|Articles with example pseudocode                                             |6    |
|Year of birth missing (living people)                                        |6    |
|Java platform                                                                |6    |
|Software architecture                                                        |5    |
+-----------------------------------------------------------------------------+-----+
only showing top 50 rows
```

Code can be found [here](./spark_scala/src/main/scala/Wikipedia.scala).

## Activity #7: Spark Streaming (File Streaming + Kafka Streaming)

### Implement Apacha Spark File Streaming 

We'll be using Spark's [structured
streaming](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html)
for this.

For this, we'll use the MinIO server that we previously deployed. A new "data"
bucket will be created. We also create an access key. Finally, we upload the
`users.csv` file that was previously used.

Let's add the Hadoop S3 library first:

```scala
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.3.1"
```

Then, let's configure the Spark S3 client:

```scala
spark
  .sparkContext
  .hadoopConfiguration.set("fs.s3a.access.key", "oILqW674otsOTzwOb7VQ")

spark
  .sparkContext
  .hadoopConfiguration.set("fs.s3a.secret.key", "VNwFyUZQHcZckKskE1bkTP1fi9o8SyvbuQ2bOpPQ")

spark
  .sparkContext
  .hadoopConfiguration.set("fs.s3a.endpoint", "http://localhost:9000")

spark
  .sparkContext
  .hadoopConfiguration.set("fs.s3a.path.style.access", "true")
```

> [!NOTE]
> Replace the credentials with the ones you generated on MinIO.

Finally, we can create a stream to read all files from the "data" S3 bucket:

```scala
val schema = new StructType()
  .add("id", IntegerType)
  .add("name", StringType)
  .add("age", IntegerType)
  .add("city", StringType)

val stream = spark.readStream
  .format("csv")
  .option("header", "true")
  .schema(schema)
  .load("s3a://data/")

stream.writeStream
  .format("console")
  .outputMode("append")
  .trigger(Trigger.ProcessingTime("1 second"))
  .start()
  .awaitTermination()
```

Result:

```
-------------------------------------------
Batch: 0
-------------------------------------------
+---+----+---+-------------+
| id|name|age|         city|
+---+----+---+-------------+
|  1|  P1| 28|     New York|
|  2|  P2| 22|San Francisco|
|  3|  P3| 30|     New York|
|  4|  P4| 25|       Boston|
|  5|  P5| 35|San Francisco|
+---+----+---+-------------+
```

Code can be found [here](./spark_scala/src/main/scala/StreamS3.scala).

### Implement Apache Spark With Kafka Streaming

#### Deploy Kafka into the Kubernetes cluster

For this part, we will be using the k3d cluster previously created.

First, let's install the Strimizi operator:

```sh
$ kubectl create namespace kafka
namespace/kafka created

$ kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
customresourcedefinition.apiextensions.k8s.io/kafkamirrormaker2s.kafka.strimzi.io created
customresourcedefinition.apiextensions.k8s.io/strimzipodsets.core.strimzi.io created
customresourcedefinition.apiextensions.k8s.io/kafkanodepools.kafka.strimzi.io created
customresourcedefinition.apiextensions.k8s.io/kafkamirrormakers.kafka.strimzi.io created
...
```

After applying the resources for the Strimizi operator, wait a few minutes for
the operator to be ready.

Once everything is ready, create the Kafka cluster:

```sh
$ kubectl apply -f https://strimzi.io/examples/latest/kafka/kraft/kafka-single-node.yaml -n kafka 
kafkanodepool.kafka.strimzi.io/dual-role created
kafka.kafka.strimzi.io/my-cluster created
```

#### Read data from Kafka in Spark

We'll be using Spark Structured Streaming's [Kafka
integration](https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html).

Let's add the Spark SQL Kafka integration library first:

```scala
libraryDependencies += "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % "3.5.5"
```

Then, let's create a stream to read what's sent on topics whose name starts
with "topic":

```scala
val stream = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "my-cluster-kafka-bootstrap.kafka.svc.cluster.local:9092")
  .option("subscribePattern", "topic*")
  .load()

stream.writeStream
  .format("console")
  .outputMode("append")
  .trigger(Trigger.ProcessingTime("1 second"))
  .start()
  .awaitTermination()
```

This Spark application is deployed in the Kubernetes cluster using the Spark
Operator so it can talk with the Kafka brokers:

```sh
$ kubectl apply -f spark_kube/spark-kafka.yaml 
sparkapplication.sparkoperator.k8s.io/spark-kafka configured
```

Code can be found [here](./spark_scala/src/main/scala/StreamKafka.scala).
