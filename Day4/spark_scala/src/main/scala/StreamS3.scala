package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}

object StreamS3 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

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
  }
}