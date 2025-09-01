package pe.edu.vallegrande.projectPruebasApis.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.vallegrande.projectPruebasApis.entity.MovieEntity;

public interface MovieRepository extends ReactiveCrudRepository<MovieEntity, Long> {
}