# Proyecto Spring WebFlux con APIs RapidAPI y PostgreSQL

Este proyecto es una aplicación Spring WebFlux que consume dos APIs de RapidAPI:

1. AI Movie Recommender API - Para buscar y obtener información de películas
2. JSearch API - Para buscar trabajos en diferentes países

Los datos obtenidos de estas APIs se almacenan en una base de datos PostgreSQL alojada en Neon.

## Tecnologías utilizadas en este proyeto son muy buenas ijijii

- Spring Boot 3.x
- Spring WebFlux
- Spring Data R2DBC
- PostgreSQL (Neon)
- RapidAPI

## Configuración

El proyecto utiliza las siguientes configuraciones:

- Base de datos PostgreSQL alojada en Neon
- API Key de RapidAPI para acceder a las APIs

## Endpoints disponibles

### AI Movie Recommender API

- `GET /api/movies/search?title=La%20La%20Land`: Busca películas por título
- `GET /api/movies/getID?title=La%20La%20Land`: Obtiene información detallada de una película por título
- `GET /api/movies`: Obtiene todas las películas guardadas en la base de datos

### JSearch API

- `GET /api/jobs/search?query=developer%20jobs%20in%20chicago&country=us&page=1&numPages=1`: Busca trabajos con parámetros de búsqueda
- `GET /api/jobs`: Obtiene todos los trabajos guardados en la base de datos

## Ejemplos de uso

### Buscar películas

```bash
curl http://localhost:8080/api/movies/search?title=La%20La%20Land


## 🚀 Inicio Rápido con Docker Compose

Este proyecto incluye una configuración completa de Docker Compose que integra tanto el backend como el frontend en un solo stack.

### Opción 1: Usando Docker Compose (Recomendado)

```bash
# 1. Copiar el archivo de variables de entorno
cp .env.example .env

# 2. Editar .env con tus credenciales
# (Base de datos, RapidAPI key, etc.)

# 3. Iniciar el stack completo
docker-compose up -d

# 4. Acceder a los servicios
# Frontend: http://localhost:3000
# Backend: http://localhost:9086
# Swagger: http://localhost:9086/swagger-ui.html
```

### Opción 2: Usando scripts de inicio

**Linux/Mac:**
```bash
chmod +x start-fullstack.sh
./start-fullstack.sh
```

**Windows:**
```cmd
start-fullstack.cmd
```

## 📚 Documentación Adicional

- [Guía completa de Docker Compose](DOCKER-FULLSTACK.md)
- [Configuración para GitHub Codespaces](CODESPACES-SETUP.md)
- [Comandos de ejecución](COMANDOS-EJECUTAR.md)
- [Inicio rápido](INICIO-RAPIDO.md)

## 🌐 Despliegue en GitHub Codespaces

Para desplegar en Codespaces, solo necesitas dos archivos:

1. `docker-compose.yml` - Configuración de servicios
2. `.env` - Variables de entorno (crear desde `.env.example`)

Ver la [guía completa de Codespaces](CODESPACES-SETUP.md) para más detalles.

## 🔧 Comandos Útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f

# Detener servicios
docker-compose down

# Reiniciar servicios
docker-compose restart

# Ver estado de los servicios
docker-compose ps

# Reconstruir imágenes
docker-compose up -d --build
```

## 🏗️ Arquitectura

El proyecto utiliza una arquitectura de microservicios con:

- **Backend**: Spring Boot WebFlux (Puerto 9086)
- **Frontend**: Nginx + React/Angular (Puerto 3000)
- **Base de datos**: PostgreSQL en Neon (R2DBC)
- **Red**: `fullstack-network` para comunicación entre servicios

```
┌─────────────────┐
│   Frontend      │
│   (Port 3000)   │
└────────┬────────┘
         │
         │ /api/* proxy
         │
┌────────▼────────┐
│   Backend       │
│   (Port 9086)   │
└────────┬────────┘
         │
         │ R2DBC
         │
┌────────▼────────┐
│   PostgreSQL    │
│   (Neon Cloud)  │
└─────────────────┘
```

## 🔐 Variables de Entorno

Las principales variables que debes configurar en `.env`:

```env
# Backend
SERVER_PORT=9086
APP_HOST=http://localhost

# Frontend
FRONTEND_PORT=3000

# Base de datos
SPRING_R2DBC_URL=r2dbc:postgresql://tu-host:5432/tu-db
SPRING_R2DBC_USERNAME=tu_usuario
SPRING_R2DBC_PASSWORD=tu_password

# RapidAPI
RAPIDAPI_KEY=tu_api_key
```

Ver `.env.example` para la lista completa de variables.
