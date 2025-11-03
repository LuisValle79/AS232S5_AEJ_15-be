#!/bin/bash

# Script para actualizar el puerto en todos los archivos de configuración de Kubernetes
# Uso: ./update-port.sh <nuevo_puerto>

if [ $# -eq 0 ]; then
    echo "Uso: $0 <nuevo_puerto>"
    exit 1
fi

NEW_PORT=$1

echo "Actualizando el puerto a $NEW_PORT en todos los archivos de configuración..."

# Actualizar el ConfigMap
sed -i "s/server.port: \"[0-9]*\"/server.port: \"$NEW_PORT\"/" k8s-manifests/15-luis-valle-configmap.yml

# Actualizar el Deployment
sed -i "s/containerPort: [0-9]*/containerPort: $NEW_PORT/" k8s-manifests/15-luis-valle-deployment.yml
sed -i "s/port: [0-9]*/port: $NEW_PORT/" k8s-manifests/15-luis-valle-deployment.yml
sed -i "s/targetPort: [0-9]*/targetPort: $NEW_PORT/" k8s-manifests/15-luis-valle-deployment.yml

# Actualizar el Service
sed -i "s/port: [0-9]*/port: $NEW_PORT/" k8s-manifests/15-luis-valle-service.yml
sed -i "s/targetPort: [0-9]*/targetPort: $NEW_PORT/" k8s-manifests/15-luis-valle-service.yml

echo "Puerto actualizado a $NEW_PORT en todos los archivos."
echo "Recuerda aplicar los cambios con:"
echo "  kubectl apply -f k8s-manifests/15-luis-valle-configmap.yml"
echo "  kubectl apply -f k8s-manifests/15-luis-valle-deployment.yml"
echo "  kubectl apply -f k8s-manifests/15-luis-valle-service.yml"
echo "  kubectl delete pods --all -n 15-luis-valle-namespace"