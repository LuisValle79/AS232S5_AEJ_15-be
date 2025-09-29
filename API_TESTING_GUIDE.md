# 🧪 Guía de Testing para Endpoints de Eliminación Lógica y Restauración

## 📋 Comandos cURL para Testing

### 🎬 PELÍCULAS - Comandos de Testing

#### 1. Crear una película de prueba
```bash
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": "test_movie_001",
    "title": "Película de Prueba",
    "overview": "Esta es una película de prueba para testing",
    "posterPath": "/test-poster.jpg",
    "releaseDate": "2024-01-01",
    "voteAverage": 8.5,
    "voteCount": 100,
    "popularity": 95.0,
    "genreIds": "28,12"
  }'
```

#### 2. Listar todas las películas activas
```bash
curl -X GET http://localhost:8080/api/movies
```

#### 3. Obtener película por ID
```bash
curl -X GET http://localhost:8080/api/movies/1
```

#### 4. Eliminar película (lógico)
```bash
curl -X DELETE http://localhost:8080/api/movies/1
```

#### 5. 🎯 NUEVO: Listar películas eliminadas
```bash
curl -X GET http://localhost:8080/api/movies/deleted
```

#### 6. 🎯 NUEVO: Obtener película eliminada por ID
```bash
curl -X GET http://localhost:8080/api/movies/deleted/1
```

#### 7. 🎯 RESTAURAR película
```bash
curl -X PATCH http://localhost:8080/api/movies/1/restore
```

#### 8. Actualizar película
```bash
curl -X PUT http://localhost:8080/api/movies/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Película Actualizada",
    "overview": "Descripción actualizada",
    "posterPath": "/nuevo-poster.jpg",
    "releaseDate": "2024-02-01",
    "voteAverage": 9.0,
    "voteCount": 200,
    "popularity": 98.0,
    "genreIds": "28"
  }'
```

#### 9. Buscar películas por título (local)
```bash
curl -X GET "http://localhost:8080/api/movies/search-by-title?title=Prueba"
```

#### 10. Buscar películas (API externa)
```bash
curl -X GET "http://localhost:8080/api/movies/search?title=Avengers"
```

---

### 💼 TRABAJOS - Comandos de Testing

#### 1. Crear un trabajo de prueba
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": "test_job_001",
    "employerName": "Empresa de Prueba",
    "jobTitle": "Desarrollador de Prueba",
    "jobDescription": "Este es un trabajo de prueba para testing",
    "jobCountry": "Peru",
    "jobCity": "Lima",
    "jobPostedAt": "2024-01-01T12:00:00",
    "jobApplyLink": "https://test-apply.com",
    "jobEmploymentType": "Full-time"
  }'
```

#### 2. Listar todos los trabajos activos
```bash
curl -X GET http://localhost:8080/api/jobs
```

#### 3. Obtener trabajo por ID
```bash
curl -X GET http://localhost:8080/api/jobs/1
```

#### 4. Eliminar trabajo (lógico)
```bash
curl -X DELETE http://localhost:8080/api/jobs/1
```

#### 5. 🎯 NUEVO: Listar trabajos eliminados
```bash
curl -X GET http://localhost:8080/api/jobs/deleted
```

#### 6. 🎯 NUEVO: Obtener trabajo eliminado por ID
```bash
curl -X GET http://localhost:8080/api/jobs/deleted/1
```

#### 7. 🎯 RESTAURAR trabajo
```bash
curl -X PATCH http://localhost:8080/api/jobs/1/restore
```

#### 8. Actualizar trabajo
```bash
curl -X PUT http://localhost:8080/api/jobs/1 \
  -H "Content-Type: application/json" \
  -d '{
    "employerName": "Nueva Empresa",
    "jobTitle": "Nuevo Título",
    "jobDescription": "Nueva descripción del trabajo",
    "jobCountry": "Chile",
    "jobCity": "Santiago",
    "jobPostedAt": "2024-02-01T12:00:00",
    "jobApplyLink": "https://new-apply.com",
    "jobEmploymentType": "Part-time"
  }'
```

#### 9. Buscar trabajos por título
```bash
curl -X GET "http://localhost:8080/api/jobs/search-by-title?title=Desarrollador"
```

#### 10. Buscar trabajos por empresa
```bash
curl -X GET "http://localhost:8080/api/jobs/search-by-employer?employer=Empresa"
```

#### 11. Buscar trabajos por país
```bash
curl -X GET "http://localhost:8080/api/jobs/search-by-country?country=Peru"
```

#### 12. Buscar trabajos por ciudad
```bash
curl -X GET "http://localhost:8080/api/jobs/search-by-city?city=Lima"
```

#### 13. Buscar trabajos (API externa)
```bash
curl -X GET "http://localhost:8080/api/jobs/search?query=developer&country=us&page=1&numPages=1"
```

---

## 🔍 Secuencia de Testing Completa

### 📝 Testing del Flujo de Eliminación y Restauración

#### Para Películas:
```bash
# 1. Crear película
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{"movieId": "test_001", "title": "Test Movie", "overview": "Test"}'

# 2. Verificar que aparece en lista activa
curl -X GET http://localhost:8080/api/movies

# 3. Eliminar la película (lógico)
curl -X DELETE http://localhost:8080/api/movies/1

# 4. Verificar que NO aparece en lista activa
curl -X GET http://localhost:8080/api/movies

# 5. Verificar que SÍ aparece en lista eliminada
curl -X GET http://localhost:8080/api/movies/deleted

# 6. Restaurar la película
curl -X PATCH http://localhost:8080/api/movies/1/restore

# 7. Verificar que vuelve a aparecer en lista activa
curl -X GET http://localhost:8080/api/movies

# 8. Verificar que ya NO aparece en lista eliminada
curl -X GET http://localhost:8080/api/movies/deleted
```

#### Para Trabajos:
```bash
# 1. Crear trabajo
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"jobId": "test_001", "employerName": "Test Co", "jobTitle": "Test Job", "jobCountry": "US", "jobCity": "NYC"}'

# 2. Verificar en lista activa
curl -X GET http://localhost:8080/api/jobs

# 3. Eliminar el trabajo
curl -X DELETE http://localhost:8080/api/jobs/1

# 4. Verificar en lista eliminada
curl -X GET http://localhost:8080/api/jobs/deleted

# 5. Restaurar el trabajo
curl -X PATCH http://localhost:8080/api/jobs/1/restore

# 6. Verificar que volvió a activos
curl -X GET http://localhost:8080/api/jobs
```

---

## 🚫 Testing de Casos de Error

### 1. Intentar restaurar un elemento que ya está activo
```bash
# Esto debería devolver error "ALREADY_ACTIVE"
curl -X PATCH http://localhost:8080/api/movies/1/restore
```

### 2. Intentar obtener un elemento que no existe
```bash
# Esto debería devolver 404
curl -X GET http://localhost:8080/api/movies/99999
```

### 3. Intentar crear con ID duplicado
```bash
# Primero crear
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{"movieId": "duplicate_test", "title": "First"}'

# Luego intentar crear otro con mismo movieId
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{"movieId": "duplicate_test", "title": "Second"}'
```

### 4. Intentar eliminar un elemento que no existe
```bash
# Esto debería devolver error NOT_FOUND
curl -X DELETE http://localhost:8080/api/movies/99999
```

---

## 📊 Respuestas Esperadas

### Respuesta de Éxito (Crear/Actualizar)
```json
{
  "success": true,
  "message": "Película creada exitosamente",
  "data": {
    "id": 1,
    "movieId": "test_001",
    "title": "Test Movie",
    "isActive": true,
    // ... otros campos
  },
  "hasData": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Respuesta de Éxito (Eliminar/Restaurar)
```json
{
  "success": true,
  "message": "Película eliminada exitosamente",
  "data": null,
  "hasData": false,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Respuesta de Error
```json
{
  "success": false,
  "message": "Ya existe una película con ese ID",
  "data": null,
  "hasData": false,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Respuesta 404 (No Encontrado)
```http
HTTP/1.1 404 Not Found
Content-Length: 0
```

---

## 🎯 Checklist de Validación

### ✅ Funcionalidades a Verificar:

#### Películas:
- [ ] ✅ Crear película nueva
- [ ] ✅ Listar películas activas
- [ ] ✅ Obtener película por ID
- [ ] ✅ Actualizar película
- [ ] ✅ Eliminar película (lógico)
- [ ] ✅ Listar películas eliminadas
- [ ] ✅ Obtener película eliminada por ID
- [ ] ✅ Restaurar película
- [ ] ✅ Buscar películas por título
- [ ] ✅ Error al duplicar movieId
- [ ] ✅ Error al restaurar película activa

#### Trabajos:
- [ ] ✅ Crear trabajo nuevo
- [ ] ✅ Listar trabajos activos
- [ ] ✅ Obtener trabajo por ID
- [ ] ✅ Actualizar trabajo
- [ ] ✅ Eliminar trabajo (lógico)
- [ ] ✅ Listar trabajos eliminados
- [ ] ✅ Obtener trabajo eliminado por ID
- [ ] ✅ Restaurar trabajo
- [ ] ✅ Buscar trabajos (todos los tipos)
- [ ] ✅ Error al duplicar jobId
- [ ] ✅ Error al restaurar trabajo activo

---

## 🚀 Uso para Frontend

Una vez validado que todo funciona con cURL, puedes usar exactamente las mismas URLs y payloads en tu frontend con:
- **fetch()** en JavaScript vanilla
- **axios** en React/Vue/Angular
- **HttpClient** en Angular
- **Cualquier librería HTTP** de tu preferencia

**¡El backend está listo para tu frontend!** 🎉