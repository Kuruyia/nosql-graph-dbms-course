package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.collect_list

object UserTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val usersData = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("data/users.csv")

    val result = usersData
      .filter($"age" >= 25)
      .groupBy("city")
      .agg(collect_list("name").as("names"))
      .select("city", "names")

    val resultCollected = result.collect()
    resultCollected.foreach { row =>
      val city = row.getAs[String]("city")
      val names = row.getAs[Seq[String]]("names")
      println(s"Users in $city: ${names.mkString(", ")}")
    }

    spark.stop()
  }
}
