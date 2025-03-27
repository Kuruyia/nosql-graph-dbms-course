package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession

object SimpleApp {
  def main(args: Array[String]): Unit = {
    val logFile = "data/SPARK_README.md"
    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .master("local[*]")
      .getOrCreate()

    val logData = spark.read.textFile(logFile).cache()
    val numData = logData.filter(line => line.contains("data")).count()
    val numSpark = logData.filter(line => line.contains("spark")).count()
    println(s"Lines with data: $numData, Lines with spark: $numSpark")
    spark.stop()
  }
}