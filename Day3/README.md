# NoSQL Graph DBMS - Day 3 activities

## Activity #1: Run a Scala worksheet

In IntelliJ Idea, we run the following Scala worksheet:

```scala
// Create a list
val list: List[Int] = List(1, 2, 3, 4, 5)

// Test the prepend operation
-1 :: list
```

Result:
```

list: List[Int] = List(1, 2, 3, 4, 5)


res0: List[Int] = List(-1, 1, 2, 3, 4, 5)
```

Code can be found [here](./spark_scala/src/main/scala/Day3-1.sc).

## Activity #2: Run the spark quick start program on Kubernetes using Spark operator

First, let's create a local Kubernetes cluster using [k3d](https://k3d.io/stable/):

```sh
$ k3d cluster create
INFO[0000] Prep: Network                                
INFO[0000] Created network 'k3d-k3s-default'            
INFO[0000] Created image volume k3d-k3s-default-images  
INFO[0000] Starting new tools node...                   
INFO[0001] Creating node 'k3d-k3s-default-server-0'     
INFO[0001] Pulling image 'ghcr.io/k3d-io/k3d-tools:5.8.3' 
INFO[0002] Pulling image 'docker.io/rancher/k3s:v1.31.5-k3s1' 
INFO[0008] Starting node 'k3d-k3s-default-tools'        
INFO[0021] Creating LoadBalancer 'k3d-k3s-default-serverlb' 
INFO[0023] Pulling image 'ghcr.io/k3d-io/k3d-proxy:5.8.3' 
INFO[0131] Using the k3d-tools node to gather environment information 
INFO[0131] HostIP: using network gateway 192.168.97.1 address 
INFO[0131] Starting cluster 'k3s-default'               
INFO[0131] Starting servers...                          
INFO[0131] Starting node 'k3d-k3s-default-server-0'     
INFO[0133] All agents already running.                  
INFO[0133] Starting helpers...                          
INFO[0133] Starting node 'k3d-k3s-default-serverlb'     
INFO[0140] Injecting records for hostAliases (incl. host.k3d.internal) and for 2 network members into CoreDNS configmap... 
INFO[0142] Cluster 'k3s-default' created successfully!  
INFO[0142] You can now use it like this:                
kubectl cluster-info
```

Once the cluster is ready, we can deploy the [Spark
Operator](https://www.kubeflow.org/docs/components/spark-operator/getting-started/)
by following the instructions found in the "Getting Started":

```sh
$ helm repo add spark-operator https://kubeflow.github.io/spark-operator
"spark-operator" has been added to your repositories

$ helm repo update
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "spark-operator" chart repository
[...]

$ helm install spark-operator spark-operator/spark-operator \
    --namespace spark-operator --create-namespace --wait
NAME: spark-operator
LAST DEPLOYED: Wed Mar 26 09:25:13 2025
NAMESPACE: spark-operator
STATUS: deployed
REVISION: 1
TEST SUITE: None
```

Finally, we can deploy the quick start program:

```sh
$ kubectl apply -f ./spark_kube/spark-pi.yaml
sparkapplication.sparkoperator.k8s.io/spark-pi created
```

Let's make sure that the application has run successfully:

```sh
$ kubectl get SparkApplication spark-pi
NAME       STATUS      ATTEMPTS   START                  FINISH                 AGE
spark-pi   COMPLETED   1          2025-03-26T08:26:47Z   2025-03-26T08:27:07Z   46s
```

## Activity #2.1: Use Minio

### Deploy Minio

First, let's install the Minio Helm chart from Bitnami:

```sh
$ helm install minio oci://registry-1.docker.io/bitnamicharts/minio
Pulled: registry-1.docker.io/bitnamicharts/minio:15.0.7
Digest: sha256:817c589dae2ecba451b61493f8864467503f66f88b6ab955e9ee065ade0e631e
NAME: minio
LAST DEPLOYED: Wed Mar 26 09:57:22 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
CHART NAME: minio
CHART VERSION: 15.0.7
APP VERSION: 2025.3.12
[...]
```

Once Minio is deployed, we can get the credentials to access it using the web
interface:

```sh
$ kubectl get Secret minio -o jsonpath='{.data.root-user}' | base64 --decode
admin

$ kubectl get Secret minio -o jsonpath='{.data.root-password}' | base64 --decode
<password>
```

Then, do a port-forward to expose the Minio web interface to the host:

```sh
$ kubectl port-forward svc/minio 9001:9001
Forwarding from 127.0.0.1:9001 -> 9001
Forwarding from [::1]:9001 -> 9001
```

Minio can now be accessed on http://localhost:9001

### Upload the data files to Minio

Once you are logged into the Minio web interface, create a bucket named "do5":

![Minio bucket creation](./assets/minio-bucket-create.png)

Then, let's upload the data files contained in [`spark_scala/data/`](./spark_scala/data/):

![Minio data files](./assets/minio-data-files.png)

### Build a `.jar` file from the Scala application and upload it

To build a JAR application from the Scala Spark application, simply use sbt
while in the [`spark_scala/`](./spark_scala/) directory:

```sh
$ sbt assembly
```

The built JAR file will be available at
`target/scala-2.12/spark_scala-assembly-0.1.0-SNAPSHOT.jar`.

Upload it to the bucket that was previously created:

![Minio JAR file](./assets/minio-jar-file.png)

### Allow anonymous access to the bucket

In the bucket administrative settings, add a read-only anonymous access rule to
the whole bucket:

![Minio anonymous access rule](./assets/minio-anonymous.png)

### Run the Spark application

Once everything has been uploaded to the S3 bucket, we can deploy the Spark
application to the Kubernetes cluster:

```sh
$ kubectl apply -f ./spark_kube/spark-minio.yaml
sparkapplication.sparkoperator.k8s.io/spark-minio created
```

Let's make sure that the application has run successfully:

```sh
$ kubectl get SparkApplication spark-minio
NAME          STATUS      ATTEMPTS   START                  FINISH                 AGE
spark-minio   COMPLETED   1          2025-03-26T10:57:57Z   2025-03-26T10:58:05Z   9m5s
```

We can check the log of the Spark application to get the results:

```sh
$ kubectl logs pod/spark-minio-driver | grep 'Users in'
Users in San Francisco: P5
Users in New York: P1, P3
Users in Boston: P4
```

## Activity #3: Stackoverflow data analysis

### Counting the number of lines in the CSV

Here's the code to do that:

```scala
val data = spark.read
  .option("header", "false")
  .csv("data/stackoverflow.csv")

println(f"Count: ${data.count()}")
```

Result:

```
Count: 4000000
```

Code can be found [here](./spark_scala/src/main/scala/StackOverflowCount.scala).

### Parsing the CSV file

Here's the code to do that:

```scala
val schema = new StructType()
  .add("postTypeId", IntegerType, nullable = true)
  .add("id", IntegerType, nullable = true)
  .add("acceptedAnswer", StringType, nullable = true)
  .add("parentId", IntegerType, nullable = true)
  .add("score", IntegerType, nullable = true)
  .add("tag", StringType, nullable = true)

val df = spark.read
  .option("header", "false")
  .schema(schema)
  .csv(csvDataFile)
  .drop("acceptedAnswer")

df.printSchema()
df.show(5)
```

Result:

```
root
 |-- postTypeId: integer (nullable = true)
 |-- id: integer (nullable = true)
 |-- parentId: integer (nullable = true)
 |-- score: integer (nullable = true)
 |-- tag: string (nullable = true)

+----------+--------+--------+-----+-----------+
|postTypeId|      id|parentId|score|        tag|
+----------+--------+--------+-----+-----------+
|         1|27233496|    NULL|    0|         C#|
|         1|23698767|    NULL|    9|         C#|
|         1| 5484340|    NULL|    0|         C#|
|         2| 5494879| 5484340|    1|       NULL|
|         1| 9419744|    NULL|    2|Objective-C|
+----------+--------+--------+-----+-----------+
only showing top 5 rows
```

Code can be found [here](./spark_scala/src/main/scala/StackOverflow.scala).

### Filter posts with a score greater than 20

Here's the code:

```scala
val highScorePosts = df.filter(col("score") > 20)
highScorePosts.show(5)
```

Result:

```
+----------+--------+--------+-----+------+
|postTypeId|      id|parentId|score|   tag|
+----------+--------+--------+-----+------+
|         1| 3627793|    NULL|   45|Python|
|         2| 3627835| 3627793|   31|  NULL|
|         1| 5667358|    NULL|   28|    C#|
|         1|10177666|    NULL|   26| Scala|
|         1| 2614473|    NULL|   37|  Java|
+----------+--------+--------+-----+------+
only showing top 5 rows
```

### Top 5 highest scores using SQL

Here's the code:

```scala
df.createOrReplaceTempView("stackoverflow")

val top5Scores = spark.sql("SELECT id, score FROM stackoverflow ORDER BY score DESC LIMIT 5")
top5Scores.show()
```

Result:

```
+-------+-----+
|     id|score|
+-------+-----+
|6841479| 5904|
|1789952| 4370|
|1642028| 3798|
|1642035| 3748|
| 111102| 3487|
+-------+-----+
```

## Activity #4: Spark with Neo4j

To begin, we add a dependency to the Neo4j connector for Spark:

```scala
libraryDependencies += "org.neo4j" %% "neo4j-connector-apache-spark" % "5.3.1_for_spark_3"
```

Then, we can try to query the Neo4j database for the number of nodes:

```scala
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
```

Result:

```
+--------+
|COUNT(n)|
+--------+
|      29|
+--------+
```

> [!NOTE]
> The sample code expects the following:
>
> - A Neo4j database exposed on `localhost:7687`
> - A `neo4j` user, with the `password` password
> - A `neo4j` database

Code can be found [here](./spark_scala/src/main/scala/Neo4j.scala).

## Activity #5: Spark with MongoDB

For this activity, we start a MongoDB database via Docker:

```sh
$ docker run -p 27017:27017 --rm --name mongodb mongodb/mongodb-community-server
```

To begin, we add a dependency to the MongoDB connector for Spark:

```scala
libraryDependencies += "org.mongodb.spark" % "mongo-spark-connector" % "10.4.1"
```

Then, we can try to query the MongoDB database:

```scala
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
```
