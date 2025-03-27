package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}

object StackOverflow {
  def main(args: Array[String]): Unit = {
    val csvDataFile = "data/stackoverflow.csv"

    val spark = SparkSession.builder
      .appName("Stackoverflow Application")
      .config("spark.driver.memory", "8G")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val schema = new StructType()
      .add("postTypeId", IntegerType, nullable = true)
      .add("id", IntegerType, nullable = true)
      .add("acceptedAnswer", StringType, nullable = true)
      .add("parentId", IntegerType, nullable = true)
      .add("score", IntegerType, nullable = true)
      .add("tag", StringType, nullable = true)

    val df = spark.read
      .option("header", "false")
      // .option("inferSchema", "true")
      .schema(schema)
      .csv(csvDataFile)
      .drop("acceptedAnswer")

    println(s"\nCount of records in CSV file: ${df.count()}")
    df.printSchema()
    df.show(5)

    import spark.implicits._

    println("Count tag null: "+ df.filter($"tag".isNull).count() + "\nCount parentId null: "+ df.filter($"parentId".isNull).count() )

    // Filter posts with a score greater than 20
    val highScorePosts = df.filter(col("score") > 20)
    highScorePosts.show(5)

    // Top 5 highest scores
    df.createOrReplaceTempView("stackoverflow")

    val top5Scores = spark.sql("SELECT id, score FROM stackoverflow ORDER BY score DESC LIMIT 5")
    top5Scores.show()

    spark.stop()
  }
}
