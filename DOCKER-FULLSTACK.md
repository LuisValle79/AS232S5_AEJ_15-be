# Docker Compose Fullstack - Guía de Uso

## 📋 Descripción

Este docker-compose unifica el backend (Spring Boot) y el frontend (Nginx) en un solo archivo, permitiendo ejecutar toda la aplicación con un solo comando.

## 🚀 Inicio Rápido

### En Local

```bash
# 1. Asegúrate de tener el archivo .env configurado
# 2. Ejecuta el stack completo
docker-compose up -d

# 3. Verifica que los servicios estén corriendo
docker-compose ps

# 4. Ver logs
docker-compose logs -f
```

### En GitHub Codespaces

```bash
# 1. Copia el docker-compose.yml y .env a tu Codespace
# 2. Ejecuta el stack
docker-compose up -d

# 3. Codespaces automáticamente expondrá los puertos
# Accede a través de los puertos forwarded en la UI de Codespaces
```

## 🔧 Configuración

### Archivo .env

Las variables principales que debes configurar:

```env
# Backend
SERVER_PORT=9086          # Puerto del backend
APP_HOST=http://localhost # Host de la aplicación

# Frontend
FRONTEND_PORT=3000        # Puerto del frontend
FRONTEND_IMAGE=luisvalle1/frontend-movies-jobs:latest

# Base de datos
SPRING_R2DBC_URL=r2dbc:postgresql://...
SPRING_R2DBC_USERNAME=tu_usuario
SPRING_R2DBC_PASSWORD=tu_password

# RapidAPI
RAPIDAPI_KEY=tu_api_key
```

## 🌐 Acceso a los Servicios

### Local

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:9086/api
- **Swagger UI**: http://localhost:9086/swagger-ui.html

### Codespaces

Los puertos se expondrán automáticamente. Busca en la pestaña "PORTS" de Codespaces:
- Puerto 3000 → Frontend
- Puerto 9086 → Backend

## 📡 Comunicación entre Servicios

El frontend se comunica con el backend a través de la red interna `fullstack-network`:

- El frontend hace proxy de las peticiones `/api/*` al backend
- El nginx.conf.template usa `http://backend-apis-ia:${BACKEND_PORT}`
- No es necesario exponer el backend públicamente si solo el frontend lo consume

## 🛠️ Comandos Útiles

```bash
# Iniciar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs solo del backend
docker-compose logs -f backend-apis-ia

# Ver logs solo del frontend
docker-compose logs -f frontend-movies-jobs

# Reiniciar un servicio específico
docker-compose restart backend-apis-ia

# Reconstruir imágenes (si usas build local)
docker-compose up -d --build

# Ver estado de los servicios
docker-compose ps

# Verificar health checks
docker inspect backend-apis-ia | grep -A 10 Health
```

## 🔍 Troubleshooting

### El frontend no se conecta al backend

1. Verifica que ambos servicios estén en la misma red:
   ```bash
   docker network inspect fullstack-network
   ```

2. Verifica que el backend esté healthy:
   ```bash
   docker-compose ps
   ```

3. Revisa los logs del frontend:
   ```bash
   docker-compose logs frontend-movies-jobs
   ```

### Error de red "network not found"

Si ves un error sobre la red, asegúrate de que el docker-compose.yml tenga:
```yaml
networks:
  fullstack-network:
    name: fullstack-network
    driver: bridge
```

**NO** debe tener `external: true` - la red se crea automáticamente.

### Cambiar puertos

Edita el archivo `.env`:
```env
SERVER_PORT=8080      # Cambia el puerto del backend
FRONTEND_PORT=4000    # Cambia el puerto del frontend
```

Luego reinicia:
```bash
docker-compose down
docker-compose up -d
```

## 📦 Estructura de Archivos Necesarios

Para ejecutar en cualquier entorno, solo necesitas:

```
proyecto/
├── docker-compose.yml    # Configuración de servicios
└── .env                  # Variables de entorno
```

## 🎯 Endpoints Disponibles

### Backend (Puerto 9086)

- `GET /api/movies` - Lista de películas
- `GET /api/movies/search?title=Inception` - Buscar películas
- `GET /api/jobs` - Lista de trabajos
- `GET /api/jobs/search?query=developer` - Buscar trabajos
- `GET /swagger-ui.html` - Documentación interactiva

### Frontend (Puerto 3000)

- `/` - Aplicación web
- `/api/*` - Proxy al backend

## 🔐 Seguridad

- Las credenciales sensibles están en `.env` (no commitear a git)
- Agrega `.env` a tu `.gitignore`
- Usa `.env.example` como plantilla para otros desarrolladores

## 📝 Notas

- El frontend espera a que el backend esté healthy antes de iniciar (`depends_on` con `condition: service_healthy`)
- La red `fullstack-network` se crea automáticamente
- Los health checks aseguran que los servicios estén funcionando correctamente
- El `restart: unless-stopped` asegura que los servicios se reinicien automáticamente en caso de fallo
