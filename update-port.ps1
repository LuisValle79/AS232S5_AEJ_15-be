# Script para actualizar el puerto en todos los archivos de configuración de Kubernetes
# Uso: .\update-port.ps1 -NewPort <nuevo_puerto>

param(
    [Parameter(Mandatory=$true)]
    [int]$NewPort
)

Write-Host "Actualizando el puerto a $NewPort en todos los archivos de configuración..."

# Actualizar el ConfigMap
(Get-Content k8s-manifests/15-luis-valle-configmap.yml) -replace 'server.port: "[0-9]*"', "server.port: `"$NewPort`"" | Set-Content k8s-manifests/15-luis-valle-configmap.yml

# Actualizar el Deployment
(Get-Content k8s-manifests/15-luis-valle-deployment.yml) -replace 'containerPort: [0-9]*', "containerPort: $NewPort" | Set-Content k8s-manifests/15-luis-valle-deployment.yml
(Get-Content k8s-manifests/15-luis-valle-deployment.yml) -replace 'port: [0-9]*', "port: $NewPort" | Set-Content k8s-manifests/15-luis-valle-deployment.yml
(Get-Content k8s-manifests/15-luis-valle-deployment.yml) -replace 'targetPort: [0-9]*', "targetPort: $NewPort" | Set-Content k8s-manifests/15-luis-valle-deployment.yml

# Actualizar el Service
(Get-Content k8s-manifests/15-luis-valle-service.yml) -replace 'port: [0-9]*', "port: $NewPort" | Set-Content k8s-manifests/15-luis-valle-service.yml
(Get-Content k8s-manifests/15-luis-valle-service.yml) -replace 'targetPort: [0-9]*', "targetPort: $NewPort" | Set-Content k8s-manifests/15-luis-valle-service.yml

Write-Host "Puerto actualizado a $NewPort en todos los archivos."
Write-Host "Recuerda aplicar los cambios con:"
Write-Host "  kubectl apply -f k8s-manifests/15-luis-valle-configmap.yml"
Write-Host "  kubectl apply -f k8s-manifests/15-luis-valle-deployment.yml"
Write-Host "  kubectl apply -f k8s-manifests/15-luis-valle-service.yml"
Write-Host "  kubectl delete pods --all -n 15-luis-valle-namespace"