# To deploy to Kubernetes, run commands

## Namespace

```bash
kubectl apply -f ./namespace.yaml
````

## SQL

```bash
kubectl apply -f ./sqledge-deployment.yaml
kubectl apply -f ./sqledge-service.yaml
```

## Service Bus Emulator

```bash
kubectl apply -f ./servicebus-configmap.yaml
kubectl apply -f ./servicebus-emulator-deployment.yaml
kubectl apply -f ./servicebus-emulator-service.yaml
```

## Application

### Deploy

```bash
kubectl apply -f ./app-deployment.yaml
kubectl apply -f ./app-service.yaml
```

### Delete

```bash
kubectl delete deployment app --namespace sb-emulator
```

# Helpers

```bash
kubectl cluster-info
```
