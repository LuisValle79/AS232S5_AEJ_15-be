package pe.edu.vallegrande.projectPruebasApis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.edu.vallegrande.projectPruebasApis.entity.JobEntity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.jsearch.JobSearchResponse;
import pe.edu.vallegrande.projectPruebasApis.repository.JobRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class JobSearchService {

    private final JobRepository jobRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.jsearch.host}")
    private String rapidApiJsearchHost;

    public Flux<JobEntity> searchJobs(String query, String country, Integer page, Integer numPages) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("jsearch.p.rapidapi.com")
                        .path("/search")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("num_pages", numPages)
                        .queryParam("country", country)
                        .queryParam("date_posted", "all")
                        .build())
                .header("x-rapidapi-host", rapidApiJsearchHost)
                .header("x-rapidapi-key", rapidApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(JobSearchResponse.class)
                .flatMapMany(response -> {
                    // Verificar si la respuesta es nula
                    if (response == null) {
                        return Flux.error(new ApiException(
                            "La respuesta de la API es nula para la búsqueda: " + query,
                            "JobSearch",
                            "NULL_RESPONSE"
                        ));
                    }
                    
                    // Verificar si la respuesta contiene datos válidos
                    if (response.getData() == null || response.getData().isEmpty()) {
                        return Flux.error(new ApiException(
                            "No se encontraron trabajos para la búsqueda: " + query,
                            "JobSearch",
                            "NO_JOBS_FOUND"
                        ));
                    }
                    
                    // Verificar el estado de la respuesta
                    if (response.getStatus() != null && !response.getStatus().equals("OK")) {
                        return Flux.error(new ApiException(
                            "Error en la respuesta de la API: " + response.getStatus(),
                            "JobSearch",
                            "API_RESPONSE_ERROR"
                        ));
                    }
                    
                    // Verificar que los campos necesarios estén presentes
                    boolean hasValidJobs = response.getData().stream()
                        .anyMatch(jobData -> jobData.getJob_id() != null && 
                                            jobData.getEmployer_name() != null && 
                                            jobData.getJob_title() != null);
                    
                    if (!hasValidJobs) {
                        return Flux.error(new ApiException(
                            "La respuesta de la API contiene datos incompletos para la búsqueda: " + query,
                            "JobSearch",
                            "INCOMPLETE_DATA"
                        ));
                    }
                    
                    String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    // Mapear a JobEntity y guardar en la base de datos
                    return Flux.fromIterable(response.getData())
                            .map(jobData -> JobEntity.builder()
                                    .jobId(jobData.getJob_id())
                                    .employerName(jobData.getEmployer_name())
                                    .jobTitle(jobData.getJob_title())
                                    .jobDescription(jobData.getJob_description())
                                    .jobCountry(jobData.getJob_country())
                                    .jobCity(jobData.getJob_city())
                                    .jobPostedAt(jobData.getJob_posted_at_datetime_utc())
                                    .jobApplyLink(jobData.getJob_apply_link())
                                    .jobEmploymentType(jobData.getJob_employment_type())
                                    .searchDate(currentDate)
                                    .build())
                            .flatMap(jobRepository::save); // Devolver la entidad guardada
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException wcError) {
                        if (wcError.getStatusCode().is2xxSuccessful()) {
                            return Flux.error(new ApiException(
                                "La API devolvió una respuesta exitosa pero con formato incorrecto. Por favor, inténtelo de nuevo más tarde.",
                                "JobSearch",
                                "DATA_FORMAT_ERROR"
                            ));
                        } else if (wcError.getStatusCode().value() == 404) {
                            return Flux.error(new ApiException(
                                "No se encontró el recurso solicitado en la API externa.",
                                "JobSearch",
                                "RESOURCE_NOT_FOUND"
                            ));
                        } else if (wcError.getStatusCode().value() == 429) {
                            return Flux.error(new ApiException(
                                "Se ha excedido el límite de solicitudes a la API externa. Por favor, intente más tarde.",
                                "JobSearch",
                                "RATE_LIMIT_EXCEEDED"
                            ));
                        } else {
                            return Flux.error(new ApiException(
                                "Error en la API externa: " + wcError.getStatusCode() + " - " + wcError.getStatusText(),
                                "JobSearch",
                                "API_ERROR_" + wcError.getStatusCode()
                            ));
                        }
                    }
                    return Flux.error(error instanceof ApiException
                            ? error
                            : new ApiException(
                                "No se pudo procesar la búsqueda de trabajos. Por favor, inténtelo de nuevo más tarde.",
                                "JobSearch",
                                "PROCESSING_ERROR"
                            ));
                });
    }

    public Flux<JobEntity> getAllJobs() {
        return jobRepository.findAll();
    }
}