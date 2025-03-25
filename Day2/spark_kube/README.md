# Spark on Kubernetes

First, install the [Spark Operator](https://www.kubeflow.org/docs/components/spark-operator/getting-started/):

```sh
helm repo add spark-operator https://kubeflow.github.io/spark-operator
helm repo update
helm install spark-operator spark-operator/spark-operator \
    --namespace spark-operator --create-namespace --wait
```

Then, you can deploy a Spark application:

```
kubectl apply -f spark-pi.yaml
```
