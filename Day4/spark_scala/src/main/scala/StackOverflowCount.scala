package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.collect_list

object StackOverflowCount {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .master("local[*]")
      .getOrCreate()

    val data = spark.read
      .option("header", "false")
      .csv("data/stackoverflow.csv")

    println(f"Count: ${data.count()}")

    spark.stop()
  }
}
