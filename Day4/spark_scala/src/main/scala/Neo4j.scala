package net.kuruyia.spark_scala

import org.apache.spark.sql.SparkSession

object Neo4j {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .config("neo4j.url", "bolt://localhost:7687")
      .config("neo4j.authentication.basic.username", "neo4j")
      .config("neo4j.authentication.basic.password", "password")
      .config("neo4j.database", "neo4j")
      .appName("Simple Application")
      .master("local[*]")
      .getOrCreate()

    val readQuery =
      """
      MATCH (n)
      RETURN COUNT(n)
      """

    val df = spark.read
      .format("org.neo4j.spark.DataSource")
      .option("query", readQuery)
      .load()

    df.show()

    spark.stop()
  }
}
