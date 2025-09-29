# 🎯 Documentación de API para Frontend - Sistema de Eliminación Lógica y Restauración

## 📋 Resumen Ejecutivo

Tu backend está **100% listo** para manejar películas y trabajos con eliminación lógica y restauración. Esta documentación detalla todos los endpoints disponibles para implementar estas funcionalidades en tu frontend.

### ✅ Funcionalidades Implementadas
- ✅ **CRUD Completo** (Create, Read, Update, Delete)
- ✅ **Eliminación Lógica** (soft delete con campo `is_active`)
- ✅ **Restauración de Registros** eliminados
- ✅ **Listado de Registros Eliminados** para mostrar en frontend
- ✅ **Validaciones Robustas** y manejo de errores
- ✅ **Programación Reactiva** (no bloqueante)

---

## 🏗️ Arquitectura de la API

### Base URL
```
http://localhost:8080
```

### Estructura de Respuesta Estándar
```json
{
  "success": true,
  "message": "Descripción del resultado",
  "data": { /* Datos o null */ },
  "hasData": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 🎬 ENDPOINTS PARA PELÍCULAS

### 1️⃣ LISTAR PELÍCULAS ACTIVAS
```http
GET /api/movies
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Películas obtenidas correctamente",
  "data": [
    {
      "id": 1,
      "movieId": "12345",
      "title": "Película Ejemplo",
      "overview": "Descripción de la película",
      "posterPath": "/poster.jpg",
      "releaseDate": "2024-01-01",
      "voteAverage": 8.5,
      "voteCount": 1000,
      "popularity": 95.5,
      "genreIds": "28,12,16",
      "searchDate": "2024-01-01T12:00:00",
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-01T12:00:00",
      "isActive": true
    }
  ],
  "hasData": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 2️⃣ LISTAR PELÍCULAS ELIMINADAS (NUEVA FUNCIONALIDAD)
```http
GET /api/movies/deleted
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Películas eliminadas obtenidas correctamente",
  "data": [
    {
      "id": 2,
      "movieId": "67890",
      "title": "Película Eliminada",
      "overview": "Esta película fue eliminada",
      "isActive": false,
      "updatedAt": "2024-01-01T15:30:00"
      // ... otros campos
    }
  ],
  "hasData": true
}
```

### 3️⃣ OBTENER PELÍCULA POR ID (ACTIVA)
```http
GET /api/movies/{id}
```

### 4️⃣ OBTENER PELÍCULA ELIMINADA POR ID (NUEVA FUNCIONALIDAD)
```http
GET /api/movies/deleted/{id}
```

### 5️⃣ CREAR PELÍCULA
```http
POST /api/movies
Content-Type: application/json

{
  "movieId": "unique_movie_id",
  "title": "Título de la Película",
  "overview": "Descripción opcional",
  "posterPath": "/path/to/poster.jpg",
  "releaseDate": "2024-01-01",
  "voteAverage": 8.5,
  "voteCount": 1000,
  "popularity": 95.5,
  "genreIds": "28,12,16"
}
```

### 6️⃣ ACTUALIZAR PELÍCULA
```http
PUT /api/movies/{id}
Content-Type: application/json

{
  "title": "Nuevo Título",
  "overview": "Nueva descripción",
  "posterPath": "/nuevo/poster.jpg",
  "releaseDate": "2024-02-01",
  "voteAverage": 9.0,
  "voteCount": 1500,
  "popularity": 98.0,
  "genreIds": "28,12"
}
```

### 7️⃣ ELIMINAR PELÍCULA (LÓGICO)
```http
DELETE /api/movies/{id}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Película eliminada exitosamente",
  "data": null,
  "hasData": false
}
```

### 8️⃣ RESTAURAR PELÍCULA ⭐
```http
PATCH /api/movies/{id}/restore
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Película restaurada exitosamente",
  "data": null,
  "hasData": false
}
```

### 9️⃣ BUSCAR PELÍCULAS POR TÍTULO (EN DB LOCAL)
```http
GET /api/movies/search-by-title?title=termino
```

### 🔟 BUSCAR PELÍCULAS (API EXTERNA)
```http
GET /api/movies/search?title=termino
```

---

## 💼 ENDPOINTS PARA TRABAJOS

### 1️⃣ LISTAR TRABAJOS ACTIVOS
```http
GET /api/jobs
```

### 2️⃣ LISTAR TRABAJOS ELIMINADOS (NUEVA FUNCIONALIDAD)
```http
GET /api/jobs/deleted
```

### 3️⃣ OBTENER TRABAJO POR ID (ACTIVO)
```http
GET /api/jobs/{id}
```

### 4️⃣ OBTENER TRABAJO ELIMINADO POR ID (NUEVA FUNCIONALIDAD)
```http
GET /api/jobs/deleted/{id}
```

### 5️⃣ CREAR TRABAJO
```http
POST /api/jobs
Content-Type: application/json

{
  "jobId": "unique_job_id",
  "employerName": "Nombre de la Empresa",
  "jobTitle": "Título del Trabajo",
  "jobDescription": "Descripción del trabajo",
  "jobCountry": "País",
  "jobCity": "Ciudad",
  "jobPostedAt": "2024-01-01T12:00:00",
  "jobApplyLink": "https://apply.link",
  "jobEmploymentType": "Full-time"
}
```

### 6️⃣ ACTUALIZAR TRABAJO
```http
PUT /api/jobs/{id}
Content-Type: application/json

{
  "employerName": "Nueva Empresa",
  "jobTitle": "Nuevo Título",
  "jobDescription": "Nueva descripción",
  "jobCountry": "Nuevo País",
  "jobCity": "Nueva Ciudad",
  "jobPostedAt": "2024-02-01T12:00:00",
  "jobApplyLink": "https://new-apply.link",
  "jobEmploymentType": "Part-time"
}
```

### 7️⃣ ELIMINAR TRABAJO (LÓGICO)
```http
DELETE /api/jobs/{id}
```

### 8️⃣ RESTAURAR TRABAJO ⭐
```http
PATCH /api/jobs/{id}/restore
```

### 9️⃣ BUSCAR TRABAJOS POR TÍTULO
```http
GET /api/jobs/search-by-title?title=termino
```

### 🔟 BUSCAR TRABAJOS POR EMPRESA
```http
GET /api/jobs/search-by-employer?employer=empresa
```

### 1️⃣1️⃣ BUSCAR TRABAJOS POR PAÍS
```http
GET /api/jobs/search-by-country?country=pais
```

### 1️⃣2️⃣ BUSCAR TRABAJOS POR CIUDAD
```http
GET /api/jobs/search-by-city?city=ciudad
```

### 1️⃣3️⃣ BUSCAR TRABAJOS (API EXTERNA)
```http
GET /api/jobs/search?query=termino&country=us&page=1&numPages=1
```

---

## 🚀 IMPLEMENTACIÓN EN FRONTEND

### 📱 Servicio de API (JavaScript/TypeScript)

```javascript
// api.js - Configuración base
const API_BASE_URL = 'http://localhost:8080';

const apiClient = {
  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.message || 'Error en la petición');
      }
      
      return data;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  }
};

// movieService.js - Servicio para películas
export const movieService = {
  // Obtener películas activas
  async getActiveMovies() {
    return apiClient.request('/api/movies');
  },

  // 🎯 NUEVA: Obtener películas eliminadas
  async getDeletedMovies() {
    return apiClient.request('/api/movies/deleted');
  },

  // Obtener película activa por ID
  async getMovieById(id) {
    return apiClient.request(`/api/movies/${id}`);
  },

  // 🎯 NUEVA: Obtener película eliminada por ID
  async getDeletedMovieById(id) {
    return apiClient.request(`/api/movies/deleted/${id}`);
  },

  // Crear película
  async createMovie(movieData) {
    return apiClient.request('/api/movies', {
      method: 'POST',
      body: JSON.stringify(movieData),
    });
  },

  // Actualizar película
  async updateMovie(id, movieData) {
    return apiClient.request(`/api/movies/${id}`, {
      method: 'PUT',
      body: JSON.stringify(movieData),
    });
  },

  // Eliminar película (lógico)
  async deleteMovie(id) {
    return apiClient.request(`/api/movies/${id}`, {
      method: 'DELETE',
    });
  },

  // 🎯 RESTAURAR película
  async restoreMovie(id) {
    return apiClient.request(`/api/movies/${id}/restore`, {
      method: 'PATCH',
    });
  },

  // Buscar películas por título (local)
  async searchMoviesByTitle(title) {
    return apiClient.request(`/api/movies/search-by-title?title=${encodeURIComponent(title)}`);
  },

  // Buscar películas (API externa)
  async searchMoviesExternal(title) {
    return apiClient.request(`/api/movies/search?title=${encodeURIComponent(title)}`);
  },
};

// jobService.js - Servicio para trabajos
export const jobService = {
  // Obtener trabajos activos
  async getActiveJobs() {
    return apiClient.request('/api/jobs');
  },

  // 🎯 NUEVA: Obtener trabajos eliminados
  async getDeletedJobs() {
    return apiClient.request('/api/jobs/deleted');
  },

  // Obtener trabajo activo por ID
  async getJobById(id) {
    return apiClient.request(`/api/jobs/${id}`);
  },

  // 🎯 NUEVA: Obtener trabajo eliminado por ID
  async getDeletedJobById(id) {
    return apiClient.request(`/api/jobs/deleted/${id}`);
  },

  // Crear trabajo
  async createJob(jobData) {
    return apiClient.request('/api/jobs', {
      method: 'POST',
      body: JSON.stringify(jobData),
    });
  },

  // Actualizar trabajo
  async updateJob(id, jobData) {
    return apiClient.request(`/api/jobs/${id}`, {
      method: 'PUT',
      body: JSON.stringify(jobData),
    });
  },

  // Eliminar trabajo (lógico)
  async deleteJob(id) {
    return apiClient.request(`/api/jobs/${id}`, {
      method: 'DELETE',
    });
  },

  // 🎯 RESTAURAR trabajo
  async restoreJob(id) {
    return apiClient.request(`/api/jobs/${id}/restore`, {
      method: 'PATCH',
    });
  },

  // Búsquedas locales
  async searchJobsByTitle(title) {
    return apiClient.request(`/api/jobs/search-by-title?title=${encodeURIComponent(title)}`);
  },

  async searchJobsByEmployer(employer) {
    return apiClient.request(`/api/jobs/search-by-employer?employer=${encodeURIComponent(employer)}`);
  },

  async searchJobsByCountry(country) {
    return apiClient.request(`/api/jobs/search-by-country?country=${encodeURIComponent(country)}`);
  },

  async searchJobsByCity(city) {
    return apiClient.request(`/api/jobs/search-by-city?city=${encodeURIComponent(city)}`);
  },

  // Búsqueda externa
  async searchJobsExternal(query, country = 'us', page = 1, numPages = 1) {
    return apiClient.request(`/api/jobs/search?query=${encodeURIComponent(query)}&country=${country}&page=${page}&numPages=${numPages}`);
  },
};
```

### 🎨 Componentes de UI Sugeridos

#### 1. **Lista de Elementos Eliminados**
```jsx
// DeletedItemsList.jsx (React ejemplo)
import React, { useState, useEffect } from 'react';
import { movieService, jobService } from './services/api';

const DeletedItemsList = ({ type }) => { // type: 'movies' | 'jobs'
  const [deletedItems, setDeletedItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDeletedItems();
  }, [type]);

  const loadDeletedItems = async () => {
    try {
      setLoading(true);
      const service = type === 'movies' ? movieService : jobService;
      const response = type === 'movies' 
        ? await service.getDeletedMovies()
        : await service.getDeletedJobs();
      
      setDeletedItems(response.data);
    } catch (error) {
      console.error('Error cargando elementos eliminados:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRestore = async (id) => {
    try {
      const service = type === 'movies' ? movieService : jobService;
      await (type === 'movies' ? service.restoreMovie(id) : service.restoreJob(id));
      
      // Mostrar notificación de éxito
      alert(`${type === 'movies' ? 'Película' : 'Trabajo'} restaurado exitosamente`);
      
      // Recargar lista
      loadDeletedItems();
    } catch (error) {
      console.error('Error restaurando elemento:', error);
      alert('Error al restaurar el elemento');
    }
  };

  if (loading) return <div>Cargando elementos eliminados...</div>;

  return (
    <div className="deleted-items-list">
      <h2>{type === 'movies' ? 'Películas' : 'Trabajos'} Eliminados</h2>
      
      {deletedItems.length === 0 ? (
        <p>No hay elementos eliminados</p>
      ) : (
        <div className="items-grid">
          {deletedItems.map(item => (
            <div key={item.id} className="deleted-item-card">
              <h3>{item.title || item.jobTitle}</h3>
              <p>Eliminado: {new Date(item.updatedAt).toLocaleDateString()}</p>
              
              <div className="actions">
                <button 
                  onClick={() => handleRestore(item.id)}
                  className="restore-btn"
                >
                  🔄 Restaurar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DeletedItemsList;
```

#### 2. **Botón de Eliminación con Confirmación**
```jsx
// DeleteButton.jsx
import React from 'react';

const DeleteButton = ({ itemId, itemType, itemName, onDelete }) => {
  const handleDelete = async () => {
    const confirmed = window.confirm(
      `¿Estás seguro de eliminar "${itemName}"? Podrás restaurarlo después desde la papelera.`
    );
    
    if (confirmed) {
      try {
        const service = itemType === 'movie' ? movieService : jobService;
        await (itemType === 'movie' ? service.deleteMovie(itemId) : service.deleteJob(itemId));
        
        alert('Elemento eliminado correctamente. Puedes restaurarlo desde la papelera.');
        onDelete(); // Callback para actualizar la lista
      } catch (error) {
        console.error('Error eliminando:', error);
        alert('Error al eliminar el elemento');
      }
    }
  };

  return (
    <button onClick={handleDelete} className="delete-btn">
      🗑️ Eliminar
    </button>
  );
};

export default DeleteButton;
```

#### 3. **Navegación con Indicador de Elementos Eliminados**
```jsx
// Navigation.jsx
import React, { useState, useEffect } from 'react';

const Navigation = () => {
  const [deletedCounts, setDeletedCounts] = useState({
    movies: 0,
    jobs: 0
  });

  useEffect(() => {
    loadDeletedCounts();
  }, []);

  const loadDeletedCounts = async () => {
    try {
      const [moviesResponse, jobsResponse] = await Promise.all([
        movieService.getDeletedMovies(),
        jobService.getDeletedJobs()
      ]);
      
      setDeletedCounts({
        movies: moviesResponse.data.length,
        jobs: jobsResponse.data.length
      });
    } catch (error) {
      console.error('Error cargando contadores:', error);
    }
  };

  return (
    <nav className="main-navigation">
      <ul>
        <li>
          <a href="/movies">Películas</a>
          {deletedCounts.movies > 0 && (
            <a href="/movies/deleted" className="deleted-link">
              🗑️ ({deletedCounts.movies})
            </a>
          )}
        </li>
        <li>
          <a href="/jobs">Trabajos</a>
          {deletedCounts.jobs > 0 && (
            <a href="/jobs/deleted" className="deleted-link">
              🗑️ ({deletedCounts.jobs})
            </a>
          )}
        </li>
      </ul>
    </nav>
  );
};

export default Navigation;
```

---

## ⚠️ MANEJO DE ERRORES

### Códigos de Error Comunes
| Código | Descripción | Acción Sugerida |
|--------|-------------|-----------------|
| `MOVIE_NOT_FOUND` | Película no encontrada | Verificar ID y refrescar lista |
| `JOB_NOT_FOUND` | Trabajo no encontrado | Verificar ID y refrescar lista |
| `DUPLICATE_MOVIE_ID` | ID de película duplicado | Usar ID único diferente |
| `DUPLICATE_JOB_ID` | ID de trabajo duplicado | Usar ID único diferente |
| `MOVIE_ALREADY_ACTIVE` | Película ya está activa | No necesita restauración |
| `JOB_ALREADY_ACTIVE` | Trabajo ya está activo | No necesita restauración |

### Ejemplo de Manejo de Errores
```javascript
const handleApiCall = async (apiFunction) => {
  try {
    const response = await apiFunction();
    return response;
  } catch (error) {
    // Manejo específico por tipo de error
    if (error.message.includes('NOT_FOUND')) {
      showNotification('Elemento no encontrado', 'warning');
    } else if (error.message.includes('ALREADY_ACTIVE')) {
      showNotification('El elemento ya está activo', 'info');
    } else if (error.message.includes('DUPLICATE')) {
      showNotification('Ya existe un elemento con ese ID', 'error');
    } else {
      showNotification('Error inesperado: ' + error.message, 'error');
    }
    throw error;
  }
};
```

---

## 🎯 FLUJO RECOMENDADO PARA TU FRONTEND

### 1. **Vista Principal de Películas/Trabajos**
- ✅ Lista de elementos activos
- ✅ Botón "Ver Eliminados" (con contador si hay elementos)
- ✅ Botones de acción: Editar, Eliminar
- ✅ Búsqueda y filtros

### 2. **Vista de Elementos Eliminados**
- ✅ Lista de elementos con `isActive = false`
- ✅ Información de cuándo fueron eliminados
- ✅ Botón "Restaurar" para cada elemento
- ✅ Botón "Volver" a la vista principal

### 3. **Flujo de Eliminación**
1. Usuario hace clic en "Eliminar"
2. Mostrar confirmación: "¿Eliminar? Podrás restaurarlo después"
3. Ejecutar eliminación lógica
4. Mostrar mensaje: "Eliminado correctamente. Ve a Papelera para restaurar"
5. Actualizar lista principal

### 4. **Flujo de Restauración**
1. Usuario va a vista de eliminados
2. Hace clic en "Restaurar"
3. Ejecutar restauración
4. Mostrar mensaje: "Restaurado correctamente"
5. Elemento desaparece de lista de eliminados

---

## 🚀 RESUMEN DE LO QUE TIENES LISTO

### ✅ **Backend Completamente Implementado:**
1. **Entidades** con campo `is_active` ✅
2. **Repositorios** con queries para activos/inactivos ✅
3. **Servicios** con lógica de eliminación y restauración ✅
4. **Controladores** con endpoints completos ✅
5. **Base de datos** con schema actualizado ✅

### 🎯 **Nuevos Endpoints Agregados:**
- `GET /api/movies/deleted` - Listar películas eliminadas
- `GET /api/movies/deleted/{id}` - Obtener película eliminada por ID
- `GET /api/jobs/deleted` - Listar trabajos eliminados  
- `GET /api/jobs/deleted/{id}` - Obtener trabajo eliminado por ID

### ⚡ **Todo Funciona Sin Cambios Adicionales:**
- No necesitas modificar la base de datos
- No necesitas cambiar configuraciones
- Solo implementar la UI en el frontend

---

## 🎉 CONCLUSIÓN

**¡Tu backend está 100% listo!** 🚀

Ahora puedes proceder con confianza a implementar el frontend sabiendo que tienes:
- ✅ Eliminación lógica completa
- ✅ Restauración de elementos
- ✅ Listado de elementos eliminados
- ✅ Validaciones robustas
- ✅ Manejo de errores detallado
- ✅ Programación reactiva

Solo necesitas usar los endpoints documentados arriba para crear la interfaz de usuario que permita a tus usuarios eliminar y restaurar películas y trabajos de manera individual.

**¡Éxito con tu frontend!** 💪