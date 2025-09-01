package pe.edu.vallegrande.projectPruebasApis.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.projectPruebasApis.entity.JobEntity;

@Repository
public interface JobRepository extends ReactiveCrudRepository<JobEntity, Long> {
}