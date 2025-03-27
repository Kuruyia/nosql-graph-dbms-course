ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    assembly / mainClass := Some("net.kuruyia.spark_scala.UserTestMinio"),
    name := "spark_scala",
    idePackagePrefix := Some("net.kuruyia.spark_scala")
  )

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.3"
libraryDependencies += "org.neo4j" %% "neo4j-connector-apache-spark" % "5.3.1_for_spark_3"
libraryDependencies += "org.mongodb.spark" % "mongo-spark-connector_2.12" % "10.4.1"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.3.1"
libraryDependencies += "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % "3.5.5"

ThisBuild / assemblyMergeStrategy := {
  case x if x.startsWith("META-INF/services/") => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.preferProject
}