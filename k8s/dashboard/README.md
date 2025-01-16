# Kubernetes Dashboard

## Install

```bash
helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard --create-namespace --namespace kubernetes-dashboard
kubectl -n kubernetes-dashboard port-forward svc/kubernetes-dashboard-kong-proxy 8443:443
```

## Delete

```bash
helm delete kubernetes-dashboard --namespace kubernetes-dashboard
```

## Create user

```bash
kubectl apply -f ./k8s/dashboard/service-account.yaml
kubectl apply -f ./k8s/dashboard/cluster-role-binding.yaml
```

## Create token

```bash
kubectl -n kubernetes-dashboard create token admin-user
```

## Access K8S dashboard

Go to https://localhost:8443 and enter the generated token

## References

https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md
https://github.com/kubernetes/dashboard/tree/master
