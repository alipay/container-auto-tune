#!/usr/bin/env bash

if ! [ -x "$(command -v kubectl)" ]; then
  echo "Error: kubectl is not installed. (kubectl required to install tmaestro, please install kubectl firstly.)"
  exit 1
fi

echo "===> start to mount kube config file."
kubectl get configmap tmaestro-kube-config
if [ $? -ne 0 ]; then
  kubeconfig=`ls ~/.kube/config`
  echo "${kubeconfig}"
  cmd=$(echo "kubectl create configmap tmaestro-kube-config --from-file=${kubeconfig}")
  $cmd
fi

echo "===> start deploy tmaestro."
kubectl apply -f tmaestro-lite.yaml
