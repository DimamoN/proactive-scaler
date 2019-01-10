#!/usr/bin/env bash

kubectl delete -f ./kubernetes/app-with-metrics.yaml
kubectl create -f ./kubernetes/app-with-metrics.yaml
