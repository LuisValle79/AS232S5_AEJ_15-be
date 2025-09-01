package pe.edu.vallegrande.projectPruebasApis.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.projectPruebasApis.entity.YoutubeMP3Entity;

@Repository
public interface YoutubeMP3Repository extends ReactiveCrudRepository<YoutubeMP3Entity, Long> {
}