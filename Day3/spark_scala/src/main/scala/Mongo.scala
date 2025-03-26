package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession

object Mongo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .config("spark.mongodb.read.connection.uri", "mongodb://localhost/")
      .config("spark.mongodb.write.connection.uri", "mongodb://localhost/")
      .appName("Simple Application")
      .master("local[*]")
      .getOrCreate()

    val df = spark.read
      .format("mongodb")
      .option("database", "people")
      .option("collection", "contacts")
      .load()

    df.show()

    spark.stop()
  }
}
