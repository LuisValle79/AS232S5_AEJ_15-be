# Proyecto Spring WebFlux con APIs RapidAPI y PostgreSQL

Este proyecto es una aplicación Spring WebFlux que consume dos APIs de RapidAPI:

1. AI Movie Recommender API - Para buscar y obtener información de películas
2. JSearch API - Para buscar trabajos en diferentes países

Los datos obtenidos de estas APIs se almacenan en una base de datos PostgreSQL alojada en Neon.

## Tecnologías utilizadas

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