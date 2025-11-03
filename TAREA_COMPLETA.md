# TAREA COMPLETA - MICROSERVICIO CON PUERTO CONFIGURABLE

## Información del Estudiante
- **Nombre**: Luis Valle
- **Número de Orden**: 15
- **Ciclo**: Quinto
- **Instituto**: Valle Grande

## Descripción del Proyecto
Este proyecto consiste en un microservicio basado en Spring Boot que integra APIs externas de RapidAPI para obtener información de películas y empleos, almacenándolos en una base de datos PostgreSQL en Neon. Se ha dockerizado la aplicación y desplegado en Kubernetes con configuración flexible del puerto.

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.4.4
- Spring WebFlux (reactive web)
- Spring Data R2DBC (reactive persistence)
- PostgreSQL (Neon)
- Docker
- Kubernetes

## Componentes del Proyecto

### 1. Microservicio Base
El microservicio utiliza una base de datos PostgreSQL alojada en Neon, cumpliendo con el requisito de usar una base de datos real en lugar de H2.

### 2. Dockerización en 2 Procesos
Se ha implementado un Dockerfile con construcción en dos etapas:
1. **Etapa de construcción**: Compilación del código Java usando Maven
2. **Etapa de ejecución**: Ejecución de la aplicación usando solo el JRE

### 3. Tamaño de la Imagen Docker
La imagen Docker tiene un tamaño optimizado de aproximadamente 207MB, dentro del rango requerido de 100MB-300MB.

### 4. Nombre de la Imagen
La imagen se ha nombrado como `15-luis-valle:1.0` y se ha subido a Docker Hub como `luisvalle1/15-luis-valle:1.0`.

### 5. Puerto Configurable
Se ha implementado la funcionalidad para que el puerto sea configurable a través de variables de entorno:
- Variable de entorno: `SERVER_PORT`
- Puerto por defecto: 8080
- Puerto actual configurado: 9090

## Archivos de Configuración de Kubernetes

Todos los archivos de manifiesto se encuentran en el directorio `k8s-manifests`:

### 1. Namespace ([15-luis-valle-namespace.yml](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/k8s-manifests/15-luis-valle-namespace.yml))
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: 15-luis-valle-namespace
```

### 2. Secret ([15-luis-valle-secret.yml](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/k8s-manifests/15-luis-valle-secret.yml))
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: 15-luis-valle-db-secret
  namespace: 15-luis-valle-namespace
type: Opaque
data:
  # Estos valores deben ser codificados en base64
  # echo -n 'neondb_owner' | base64
  db-username: bmVvbmRiX293bmVy
  # echo -n 'npg_yYaKwc5E6gsr' | base64
  db-password: bXBnX3lZYUt3YzVFNmdzcg==
```

### 3. ConfigMap ([15-luis-valle-configmap.yml](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/k8s-manifests/15-luis-valle-configmap.yml))
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: 15-luis-valle-config
  namespace: 15-luis-valle-namespace
data:
  server.port: "9090"
```

### 4. Service ([15-luis-valle-service.yml](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/k8s-manifests/15-luis-valle-service.yml))
```yaml
apiVersion: v1
kind: Service
metadata:
  name: luis-valle-service
  namespace: 15-luis-valle-namespace
  labels:
    app: 15-luis-valle-app
spec:
  selector:
    app: 15-luis-valle-app
  ports:
    - protocol: TCP
      port: 9090
      targetPort: 9090
  type: ClusterIP
```

### 5. Deployment ([15-luis-valle-deployment.yml](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/k8s-manifests/15-luis-valle-deployment.yml))
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: 15-luis-valle-deployment
  namespace: 15-luis-valle-namespace
  labels:
    app: 15-luis-valle-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: 15-luis-valle-app
  template:
    metadata:
      labels:
        app: 15-luis-valle-app
    spec:
      containers:
      - name: 15-luis-valle-container
        image: luisvalle1/15-luis-valle:1.0
        ports:
        - containerPort: 9090
        env:
        - name: SERVER_PORT
          valueFrom:
            configMapKeyRef:
              name: 15-luis-valle-config
              key: server.port
        - name: SPRING_R2DBC_URL
          value: "r2dbc:postgresql://ep-withered-cell-afl7k9xj-pooler.c-2.us-west-2.aws.neon.tech:5432/neondb?options=endpoint=ep-withered-cell-afl7k9xj"
        - name: SPRING_R2DBC_USERNAME
          valueFrom:
            secretKeyRef:
              name: 15-luis-valle-db-secret
              key: db-username
        - name: SPRING_R2DBC_PASSWORD
          valueFrom:
            secretKeyRef:
              name: 15-luis-valle-db-secret
              key: db-password
        - name: RAPIDAPI_KEY
          value: "ab05570ba7msh5721c89ebac0f08p1cdd10jsn93d7f9f72c33"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        readinessProbe:
          httpGet:
            path: /swagger-ui.html
            port: 9090
          initialDelaySeconds: 30
          periodSeconds: 10
        livenessProbe:
          httpGet:
            path: /swagger-ui.html
            port: 9090
          initialDelaySeconds: 60
          periodSeconds: 30
```

## Despliegue en Kubernetes

### Prerrequisitos
- Tener acceso a un clúster de Kubernetes (Minikube, GKE, EKS, AKS, etc.)
- Tener `kubectl` instalado y configurado
- Tener Docker instalado (para construir la imagen)

### Pasos para el Despliegue

1. **Construir la imagen Docker**:
   ```bash
   docker build -t luisvalle1/15-luis-valle:1.0 .
   ```

2. **Subir la imagen a Docker Hub**:
   ```bash
   docker push luisvalle1/15-luis-valle:1.0
   ```

3. **Aplicar los manifiestos de Kubernetes**:
   ```bash
   kubectl apply -f k8s-manifests/15-luis-valle-namespace.yml
   kubectl apply -f k8s-manifests/15-luis-valle-secret.yml
   kubectl apply -f k8s-manifests/15-luis-valle-configmap.yml
   kubectl apply -f k8s-manifests/15-luis-valle-service.yml
   kubectl apply -f k8s-manifests/15-luis-valle-deployment.yml
   ```

4. **Verificar el despliegue**:
   ```bash
   kubectl get pods -n 15-luis-valle-namespace
   kubectl get svc -n 15-luis-valle-namespace
   ```

### Configuración del Puerto
Para cambiar el puerto en el que se ejecuta la aplicación:

1. Editar el ConfigMap:
   ```bash
   kubectl edit configmap 15-luis-valle-config -n 15-luis-valle-namespace
   ```

2. Cambiar el valor de `server.port` al puerto deseado.

3. Reiniciar los pods para que tomen la nueva configuración:
   ```bash
   kubectl delete pods --all -n 15-luis-valle-namespace
   ```

### Scripts para Actualización Automática del Puerto
Para facilitar la actualización del puerto en todos los archivos de configuración, se han creado los siguientes scripts:

#### Script de Bash ([update-port.sh](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/update-port.sh)):
```bash
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
```

#### Script de PowerShell ([update-port.ps1](file:///c%3A/Users/luisv/OneDrive/Escritorio/Pruebas%20Apis/pruebasApis/AS232S5_AEJ_15-be/update-port.ps1)):
```powershell
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
```

### Acceso a la Aplicación
Para acceder a la aplicación desde el host local:

1. Configurar port-forwarding:
   ```bash
   kubectl port-forward service/luis-valle-service 9090:9090 -n 15-luis-valle-namespace
   ```

2. Acceder a la aplicación en:
   - Swagger UI: http://localhost:9090/swagger-ui.html
   - API de películas: http://localhost:9090/api/movies
   - API de empleos: http://localhost:9090/api/jobs

## Verificación del Funcionamiento

### Estado de los Pods
```bash
kubectl get pods -n 15-luis-valle-namespace
NAME                                         READY   STATUS    RESTARTS   AGE
15-luis-valle-deployment-5657bb56cc-985gg    1/1     Running   0          2m37s
15-luis-valle-deployment-5657bb56cc-n42lv    1/1     Running   0          97s
```

### Estado del Service
```bash
kubectl get svc -n 15-luis-valle-namespace
NAME                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
luis-valle-service   ClusterIP   10.110.87.111   <none>        9090/TCP   95m
```

## Conclusión

Se ha completado exitosamente la tarea solicitada, implementando un microservicio con las siguientes características:

1. ✅ Microservicio base con base de datos PostgreSQL en Neon
2. ✅ Dockerización en 2 procesos (construcción y ejecución separados)
3. ✅ Imagen Docker con tamaño entre 100MB y 300MB (207MB)
4. ✅ Nombre de imagen según especificaciones: `15-luis-valle:1.0`
5. ✅ Imagen subida a Docker Hub: `luisvalle1/15-luis-valle:1.0`
6. ✅ Despliegue en Kubernetes con:
   - Namespace: `15-luis-valle-namespace.yml`
   - Secret: `15-luis-valle-secret.yml`
   - Service: `15-luis-valle-service.yml`
   - Deployment: `15-luis-valle-deployment.yml`
7. ✅ Puerto configurable mediante ConfigMap
8. ✅ 2 pods en ejecución para alta disponibilidad

La aplicación está correctamente desplegada y funcionando en el puerto 9090, con todas las funcionalidades solicitadas implementadas y verificadas.

Además, se han creado scripts para facilitar la actualización del puerto en todos los archivos de configuración, permitiendo cambiar el puerto en un solo lugar y que todos los demás archivos se actualicen automáticamente.