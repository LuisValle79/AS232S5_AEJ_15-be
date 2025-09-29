package pe.edu.vallegrande.projectPruebasApis.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.vallegrande.projectPruebasApis.entity.MovieEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieRepository extends ReactiveCrudRepository<MovieEntity, Long> {
    
    // Buscar todas las películas activas (no eliminadas lógicamente)
    @Query("SELECT * FROM movies WHERE is_active = true ORDER BY created_at DESC")
    Flux<MovieEntity> findAllActive();
    
    // Buscar película activa por ID
    @Query("SELECT * FROM movies WHERE id = :id AND is_active = true")
    Mono<MovieEntity> findByIdAndActive(Long id);
    
    // Buscar películas activas por título (búsqueda parcial)
    @Query("SELECT * FROM movies WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%')) AND is_active = true ORDER BY created_at DESC")
    Flux<MovieEntity> findByTitleContainingIgnoreCaseAndActive(String title);
    
    // Buscar película activa por movie_id
    @Query("SELECT * FROM movies WHERE movie_id = :movieId AND is_active = true")
    Mono<MovieEntity> findByMovieIdAndActive(String movieId);
    
    // Eliminado lógico
    @Query("UPDATE movies SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = :id")
    Mono<Void> logicalDelete(Long id);
    
    // Restaurar película eliminada lógicamente
    @Query("UPDATE movies SET is_active = true, updated_at = CURRENT_TIMESTAMP WHERE id = :id")
    Mono<Void> restore(Long id);
    
    // Verificar si existe una película activa con el mismo movie_id
    @Query("SELECT COUNT(*) FROM movies WHERE movie_id = :movieId AND is_active = true")
    Mono<Long> countByMovieIdAndActive(String movieId);
    
    // Buscar todas las películas eliminadas lógicamente (inactivas)
    @Query("SELECT * FROM movies WHERE is_active = false ORDER BY updated_at DESC")
    Flux<MovieEntity> findAllInactive();
    
    // Buscar película eliminada por ID
    @Query("SELECT * FROM movies WHERE id = :id AND is_active = false")
    Mono<MovieEntity> findByIdAndInactive(Long id);
}