# Comandos para Ejecutar

## 1. Arreglar permisos del Dockerfile
```powershell
attrib -r Dockerfile
```

## 2. Construir la imagen
```powershell
docker build -t luisvalle1/backend-apis-ia:latest .
```

## 3. Subir a Docker Hub
```powershell
docker login
docker push luisvalle1/backend-apis-ia:latest
```

## 4. Ejecutar con Docker Compose
```powershell
docker-compose up -d
```

## 5. Ejecutar con puerto diferente (9090)
```powershell
docker-compose down
$env:SERVER_PORT=9090
docker-compose up -d
```

## 6. Detener
```powershell
docker-compose down
```

## 7. Subir a GitHub
```bash
git add .
git commit -m "feat: Docker Compose"
git push origin main
```

## Enlaces para entregar:
- Docker Hub: https://hub.docker.com/r/luisvalle1/backend-apis-ia
- GitHub: https://github.com/[tu-usuario]/[tu-repo]/blob/main/docker-compose.yml
