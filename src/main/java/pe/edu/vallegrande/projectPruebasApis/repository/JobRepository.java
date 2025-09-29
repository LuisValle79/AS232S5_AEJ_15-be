package pe.edu.vallegrande.projectPruebasApis.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.projectPruebasApis.entity.JobEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface JobRepository extends ReactiveCrudRepository<JobEntity, Long> {
    
    // Buscar todos los trabajos activos (no eliminados lógicamente)
    @Query("SELECT * FROM jobs WHERE is_active = true ORDER BY created_at DESC")
    Flux<JobEntity> findAllActive();
    
    // Buscar trabajo activo por ID
    @Query("SELECT * FROM jobs WHERE id = :id AND is_active = true")
    Mono<JobEntity> findByIdAndActive(Long id);
    
    // Buscar trabajos activos por título del trabajo (búsqueda parcial)
    @Query("SELECT * FROM jobs WHERE LOWER(job_title) LIKE LOWER(CONCAT('%', :jobTitle, '%')) AND is_active = true ORDER BY created_at DESC")
    Flux<JobEntity> findByJobTitleContainingIgnoreCaseAndActive(String jobTitle);
    
    // Buscar trabajos activos por empresa
    @Query("SELECT * FROM jobs WHERE LOWER(employer_name) LIKE LOWER(CONCAT('%', :employerName, '%')) AND is_active = true ORDER BY created_at DESC")
    Flux<JobEntity> findByEmployerNameContainingIgnoreCaseAndActive(String employerName);
    
    // Buscar trabajos activos por país
    @Query("SELECT * FROM jobs WHERE LOWER(job_country) = LOWER(:country) AND is_active = true ORDER BY created_at DESC")
    Flux<JobEntity> findByJobCountryIgnoreCaseAndActive(String country);
    
    // Buscar trabajos activos por ciudad
    @Query("SELECT * FROM jobs WHERE LOWER(job_city) = LOWER(:city) AND is_active = true ORDER BY created_at DESC")
    Flux<JobEntity> findByJobCityIgnoreCaseAndActive(String city);
    
    // Buscar trabajo activo por job_id
    @Query("SELECT * FROM jobs WHERE job_id = :jobId AND is_active = true")
    Mono<JobEntity> findByJobIdAndActive(String jobId);
    
    // Eliminado lógico
    @Query("UPDATE jobs SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = :id")
    Mono<Void> logicalDelete(Long id);
    
    // Restaurar trabajo eliminado lógicamente
    @Query("UPDATE jobs SET is_active = true, updated_at = CURRENT_TIMESTAMP WHERE id = :id")
    Mono<Void> restore(Long id);
    
    // Verificar si existe un trabajo activo con el mismo job_id
    @Query("SELECT COUNT(*) FROM jobs WHERE job_id = :jobId AND is_active = true")
    Mono<Long> countByJobIdAndActive(String jobId);
}