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
import pe.edu.vallegrande.projectPruebasApis.model.jsearch.CreateJobRequest;
import pe.edu.vallegrande.projectPruebasApis.model.jsearch.UpdateJobRequest;
import pe.edu.vallegrande.projectPruebasApis.model.jsearch.JobSearchResponse;
import pe.edu.vallegrande.projectPruebasApis.repository.JobRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    // ============ Métodos CRUD ============
    
    /**
     * Crear un nuevo trabajo
     */
    public Mono<JobEntity> createJob(CreateJobRequest request) {
        // Verificar si ya existe un trabajo activo con el mismo jobId
        return jobRepository.countByJobIdAndActive(request.getJobId())
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new ApiException(
                            "Ya existe un trabajo activo con el ID: " + request.getJobId(),
                            "JobCreate",
                            "DUPLICATE_JOB_ID"
                        ));
                    }
                    
                    String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    JobEntity job = JobEntity.builder()
                            .jobId(request.getJobId())
                            .employerName(request.getEmployerName())
                            .jobTitle(request.getJobTitle())
                            .jobDescription(request.getJobDescription())
                            .jobCountry(request.getJobCountry())
                            .jobCity(request.getJobCity())
                            .jobPostedAt(request.getJobPostedAt())
                            .jobApplyLink(request.getJobApplyLink())
                            .jobEmploymentType(request.getJobEmploymentType())
                            .searchDate(currentDate)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .isActive(true)
                            .build();
                    
                    return jobRepository.save(job);
                });
    }
    
    /**
     * Obtener trabajo por ID (solo activos)
     */
    public Mono<JobEntity> findById(Long id) {
        return jobRepository.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Trabajo no encontrado con ID: " + id,
                    "JobFind",
                    "JOB_NOT_FOUND"
                )));
    }
    
    /**
     * Actualizar trabajo
     */
    public Mono<JobEntity> updateJob(Long id, UpdateJobRequest request) {
        return jobRepository.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Trabajo no encontrado con ID: " + id,
                    "JobUpdate",
                    "JOB_NOT_FOUND"
                )))
                .map(existingJob -> {
                    existingJob.setEmployerName(request.getEmployerName());
                    existingJob.setJobTitle(request.getJobTitle());
                    existingJob.setJobDescription(request.getJobDescription());
                    existingJob.setJobCountry(request.getJobCountry());
                    existingJob.setJobCity(request.getJobCity());
                    existingJob.setJobPostedAt(request.getJobPostedAt());
                    existingJob.setJobApplyLink(request.getJobApplyLink());
                    existingJob.setJobEmploymentType(request.getJobEmploymentType());
                    existingJob.setUpdatedAt(LocalDateTime.now());
                    return existingJob;
                })
                .flatMap(jobRepository::save);
    }
    
    /**
     * Eliminado lógico de trabajo
     */
    public Mono<Void> deleteJob(Long id) {
        return jobRepository.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Trabajo no encontrado con ID: " + id,
                    "JobDelete",
                    "JOB_NOT_FOUND"
                )))
                .flatMap(job -> jobRepository.logicalDelete(id));
    }
    
    /**
     * Restaurar trabajo eliminado lógicamente
     */
    public Mono<Void> restoreJob(Long id) {
        return jobRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Trabajo no encontrado con ID: " + id,
                    "JobRestore",
                    "JOB_NOT_FOUND"
                )))
                .flatMap(job -> {
                    if (job.getIsActive()) {
                        return Mono.error(new ApiException(
                            "El trabajo ya está activo",
                            "JobRestore",
                            "JOB_ALREADY_ACTIVE"
                        ));
                    }
                    return jobRepository.restore(id);
                });
    }
    
    /**
     * Buscar trabajos por título (solo activos)
     */
    public Flux<JobEntity> findByJobTitleContaining(String jobTitle) {
        return jobRepository.findByJobTitleContainingIgnoreCaseAndActive(jobTitle);
    }
    
    /**
     * Buscar trabajos por empresa (solo activos)
     */
    public Flux<JobEntity> findByEmployerNameContaining(String employerName) {
        return jobRepository.findByEmployerNameContainingIgnoreCaseAndActive(employerName);
    }
    
    /**
     * Buscar trabajos por país (solo activos)
     */
    public Flux<JobEntity> findByCountry(String country) {
        return jobRepository.findByJobCountryIgnoreCaseAndActive(country);
    }
    
    /**
     * Buscar trabajos por ciudad (solo activos)
     */
    public Flux<JobEntity> findByCity(String city) {
        return jobRepository.findByJobCityIgnoreCaseAndActive(city);
    }
    
    /**
     * Obtener todos los trabajos activos
     */
    public Flux<JobEntity> getAllJobs() {
        return jobRepository.findAllActive();
    }
}