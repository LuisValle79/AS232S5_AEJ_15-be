# ⚡ Inicio Rápido - Luis Valle

## 🎯 Tu Información
- **Usuario Docker Hub**: `luisvalle1`
- **Imagen**: `luisvalle1/backend-apis-ia:latest`
- **Repositorio Docker Hub**: https://hub.docker.com/r/luisvalle1/backend-apis-ia

## 🚀 Comandos Esenciales (Copia y Pega)

### 1️⃣ Construir la Imagen
```powershell
docker build -t luisvalle1/backend-apis-ia:latest .
```

### 2️⃣ Probar Localmente
```powershell
docker-compose up -d
curl http://localhost:8080/api/movies
```

### 3️⃣ Subir a Docker Hub
```powershell
docker login
docker push luisvalle1/backend-apis-ia:latest
```

### 4️⃣ Probar con Puerto Diferente
```powershell
# Opción 1: Variable de entorno
docker-compose down
$env:SERVER_PORT=9090
docker-compose up -d
curl http://localhost:9090/api/movies

# Opción 2: Script
.\docker-port-update.ps1 -Port 9090
```

### 5️⃣ Subir a GitHub
```bash
git add .
git commit -m "feat: Docker Compose con variables de entorno"
git push origin main
```

## 📋 Endpoints para Probar

```bash
# Películas
curl http://localhost:8080/api/movies
curl "http://localhost:8080/api/movies/search?title=Inception"

# Trabajos
curl http://localhost:8080/api/jobs
curl "http://localhost:8080/api/jobs/search?query=developer&country=us"

# Swagger UI (navegador)
start http://localhost:8080/swagger-ui.html
```

## 🔗 Enlaces para Entregar

### 1. Docker Hub (Imagen Pública)
```
https://hub.docker.com/r/luisvalle1/backend-apis-ia
```

### 2. GitHub (docker-compose.yml)
```
https://github.com/[TU-USUARIO-GITHUB]/[TU-REPO]/blob/main/docker-compose.yml
```

**Ejemplo:**
```
https://github.com/luisvalle1/projectPruebasApis/blob/main/docker-compose.yml
```

## ✅ Checklist Rápido

```
[ ] 1. Construir imagen: docker build -t luisvalle1/backend-apis-ia:latest .
[ ] 2. Probar local: docker-compose up -d
[ ] 3. Probar endpoint: curl http://localhost:8080/api/movies
[ ] 4. Probar puerto 9090: SERVER_PORT=9090 docker-compose up -d
[ ] 5. Login Docker Hub: docker login
[ ] 6. Subir imagen: docker push luisvalle1/backend-apis-ia:latest
[ ] 7. Verificar es pública: https://hub.docker.com/r/luisvalle1/backend-apis-ia
[ ] 8. Subir a GitHub: git push origin main
[ ] 9. Obtener enlace GitHub del docker-compose.yml
[ ] 10. Entregar ambos enlaces
```

## 🎓 Ejemplos de Testeo con Diferentes Puertos

### Puerto 8080 (por defecto)
```powershell
docker-compose up -d
curl http://localhost:8080/api/movies
```

### Puerto 9090
```powershell
docker-compose down
$env:SERVER_PORT=9090
docker-compose up -d
curl http://localhost:9090/api/movies
```

### Puerto 3000
```powershell
docker-compose down
$env:SERVER_PORT=3000
docker-compose up -d
curl http://localhost:3000/api/movies
```

### Puerto 5000
```powershell
docker-compose down
$env:SERVER_PORT=5000
docker-compose up -d
curl http://localhost:5000/api/movies
```

## 📱 Accesos Rápidos

| Servicio | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/v3/api-docs |
| Movies API | http://localhost:8080/api/movies |
| Jobs API | http://localhost:8080/api/jobs |
| Docker Hub | https://hub.docker.com/r/luisvalle1/backend-apis-ia |

## 🛠️ Comandos Útiles

```powershell
# Ver logs
docker-compose logs -f

# Ver estado
docker-compose ps

# Detener
docker-compose down

# Reiniciar
docker-compose restart

# Reconstruir
docker-compose up -d --build

# Probar todos los endpoints
.\test-endpoints.ps1 -Port 8080
```

## 🆘 Solución Rápida de Problemas

| Problema | Solución |
|----------|----------|
| Docker no inicia | Abre Docker Desktop |
| Puerto ocupado | `docker-compose down` y espera 5 segundos |
| Error de login | `docker logout` y luego `docker login` |
| Imagen no encontrada | Verifica: `docker images \| findstr backend-apis-ia` |

## 📚 Documentación Completa

Si necesitas más detalles, revisa:
- **MIS-COMANDOS.md** - Todos tus comandos específicos
- **DOCKER-README.md** - Guía completa
- **PASOS-PARA-ENTREGAR.md** - Guía paso a paso
- **COMANDOS-RAPIDOS.md** - Referencia rápida

## 🎉 ¡Todo Listo!

Tu proyecto está configurado con:
- ✅ Docker Compose funcional
- ✅ Variables de entorno configurables
- ✅ Puerto configurable (8080, 9090, 3000, 5000)
- ✅ 19 endpoints GET públicos documentados
- ✅ Scripts de automatización
- ✅ Documentación completa

**Solo sigue los pasos del checklist y estarás listo para entregar!** 🚀

---

**Autor**: Luis Valle  
**Docker Hub**: luisvalle1  
**Proyecto**: Back-End de APIs IA
