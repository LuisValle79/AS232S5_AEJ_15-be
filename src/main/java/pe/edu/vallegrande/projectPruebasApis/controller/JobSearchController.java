package pe.edu.vallegrande.projectPruebasApis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.projectPruebasApis.entity.JobEntity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.ApiResponse;
import pe.edu.vallegrande.projectPruebasApis.model.jsearch.CreateJobRequest;
import pe.edu.vallegrande.projectPruebasApis.model.jsearch.UpdateJobRequest;
import pe.edu.vallegrande.projectPruebasApis.service.JobSearchService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Validated
public class JobSearchController {

    private final JobSearchService jobSearchService;

    // ============ Endpoints CRUD ============
    
    /**
     * Crear un nuevo trabajo
     */
    @PostMapping
    public Mono<ResponseEntity<ApiResponse<JobEntity>>> createJob(
            @Valid @RequestBody CreateJobRequest request) {
        
        return jobSearchService.createJob(request)
            .map(job -> {
                ApiResponse<JobEntity> response = ApiResponse.success(job, "Trabajo creado exitosamente");
                response.setHasData(true);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            })
            .onErrorResume(error -> {
                if (error instanceof ApiException apiEx) {
                    String message = switch (apiEx.getErrorCode()) {
                        case "DUPLICATE_JOB_ID" -> "Ya existe un trabajo con ese ID";
                        default -> apiEx.getMessage();
                    };
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<JobEntity>error(message)
                    ));
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<JobEntity>error("Error al crear el trabajo: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Obtener trabajo por ID
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<JobEntity>>> getJobById(@PathVariable Long id) {
        return jobSearchService.findById(id)
            .map(job -> {
                ApiResponse<JobEntity> response = ApiResponse.success(job, "Trabajo encontrado");
                response.setHasData(true);
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                if (error instanceof ApiException apiEx && "JOB_NOT_FOUND".equals(apiEx.getErrorCode())) {
                    return Mono.just(ResponseEntity.notFound().build());
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<JobEntity>error("Error al obtener el trabajo: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Actualizar trabajo
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<JobEntity>>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequest request) {
        
        return jobSearchService.updateJob(id, request)
            .map(job -> {
                ApiResponse<JobEntity> response = ApiResponse.success(job, "Trabajo actualizado exitosamente");
                response.setHasData(true);
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                if (error instanceof ApiException apiEx && "JOB_NOT_FOUND".equals(apiEx.getErrorCode())) {
                    return Mono.just(ResponseEntity.notFound().build());
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<JobEntity>error("Error al actualizar el trabajo: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Eliminar trabajo (eliminado lógico)
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteJob(@PathVariable Long id) {
        return jobSearchService.deleteJob(id)
            .then(Mono.fromCallable(() -> {
                ApiResponse<Void> response = ApiResponse.success(null, "Trabajo eliminado exitosamente");
                response.setHasData(false);
                return ResponseEntity.ok(response);
            }))
            .onErrorResume(error -> {
                if (error instanceof ApiException apiEx && "JOB_NOT_FOUND".equals(apiEx.getErrorCode())) {
                    return Mono.just(ResponseEntity.notFound().build());
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<Void>error("Error al eliminar el trabajo: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Restaurar trabajo eliminado
     */
    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<ApiResponse<Void>>> restoreJob(@PathVariable Long id) {
        return jobSearchService.restoreJob(id)
            .then(Mono.fromCallable(() -> {
                ApiResponse<Void> response = ApiResponse.success(null, "Trabajo restaurado exitosamente");
                response.setHasData(false);
                return ResponseEntity.ok(response);
            }))
            .onErrorResume(error -> {
                if (error instanceof ApiException apiEx) {
                    String message = switch (apiEx.getErrorCode()) {
                        case "JOB_NOT_FOUND" -> "Trabajo no encontrado";
                        case "JOB_ALREADY_ACTIVE" -> "El trabajo ya está activo";
                        default -> apiEx.getMessage();
                    };
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<Void>error(message)
                    ));
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<Void>error("Error al restaurar el trabajo: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Buscar trabajos por título
     */
    @GetMapping("/search-by-title")
    public Mono<ResponseEntity<ApiResponse<List<JobEntity>>>> searchJobsByTitle(
            @RequestParam String title) {
        
        if (title == null || title.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<JobEntity>>error("El título no puede estar vacío")
            ));
        }
        
        return jobSearchService.findByJobTitleContaining(title)
            .collectList()
            .map(jobs -> {
                ApiResponse<List<JobEntity>> response = ApiResponse.success(jobs, "Búsqueda realizada correctamente");
                response.setHasData(!jobs.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<JobEntity>>error("Error en la búsqueda: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Buscar trabajos por empresa
     */
    @GetMapping("/search-by-employer")
    public Mono<ResponseEntity<ApiResponse<List<JobEntity>>>> searchJobsByEmployer(
            @RequestParam String employer) {
        
        if (employer == null || employer.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<JobEntity>>error("El nombre del empleador no puede estar vacío")
            ));
        }
        
        return jobSearchService.findByEmployerNameContaining(employer)
            .collectList()
            .map(jobs -> {
                ApiResponse<List<JobEntity>> response = ApiResponse.success(jobs, "Búsqueda realizada correctamente");
                response.setHasData(!jobs.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<JobEntity>>error("Error en la búsqueda: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Buscar trabajos por país
     */
    @GetMapping("/search-by-country")
    public Mono<ResponseEntity<ApiResponse<List<JobEntity>>>> searchJobsByCountry(
            @RequestParam String country) {
        
        if (country == null || country.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<JobEntity>>error("El país no puede estar vacío")
            ));
        }
        
        return jobSearchService.findByCountry(country)
            .collectList()
            .map(jobs -> {
                ApiResponse<List<JobEntity>> response = ApiResponse.success(jobs, "Búsqueda realizada correctamente");
                response.setHasData(!jobs.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<JobEntity>>error("Error en la búsqueda: " + error.getMessage())
                ));
            });
    }
    
    /**
     * Buscar trabajos por ciudad
     */
    @GetMapping("/search-by-city")
    public Mono<ResponseEntity<ApiResponse<List<JobEntity>>>> searchJobsByCity(
            @RequestParam String city) {
        
        if (city == null || city.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<JobEntity>>error("La ciudad no puede estar vacía")
            ));
        }
        
        return jobSearchService.findByCity(city)
            .collectList()
            .map(jobs -> {
                ApiResponse<List<JobEntity>> response = ApiResponse.success(jobs, "Búsqueda realizada correctamente");
                response.setHasData(!jobs.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<JobEntity>>error("Error en la búsqueda: " + error.getMessage())
                ));
            });
    }
    
    // ============ Endpoints de API Externa (existentes) ============

    @GetMapping("/search")
    public Mono<ResponseEntity<ApiResponse<List<JobEntity>>>> searchJobs(
            @RequestParam String query,
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "1") Integer numPages) {
        
        // Validar los parámetros de entrada
        if (query == null || query.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<JobEntity>>error("El parámetro de búsqueda no puede estar vacío")
            ));
        }
        
        if (page < 1 || numPages < 1) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<JobEntity>>error("Los parámetros de paginación deben ser mayores a cero")
            ));
        }
        
        // Obtener los trabajos, recolectarlos en una lista y devolverlos
        return jobSearchService.searchJobs(query, country, page, numPages)
            .collectList()
            .map(jobs -> {
                ApiResponse<List<JobEntity>> response = ApiResponse.success(jobs, "Búsqueda de trabajos realizada correctamente");
                response.setHasData(!jobs.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                System.err.println("Error al buscar trabajos para: " + query + ". Error: " + error.getMessage());
                if (error instanceof ApiException apiError) {
                    String errorCode = apiError.getErrorCode();
                    String message = apiError.getMessage();
                    switch (errorCode) {
                        case "DATA_FORMAT_ERROR":
                            message = "El formato de la respuesta de la API no es válido. Por favor, intente con otra búsqueda.";
                            break;
                        case "INCOMPLETE_DATA":
                            message = "La información de trabajos está incompleta. Por favor, intente con otra búsqueda.";
                            break;
                        case "NO_JOBS_FOUND":
                            message = "No se encontraron trabajos para su búsqueda. Intente con otros términos o ubicación.";
                            break;
                        case "NULL_RESPONSE":
                            message = "La API no devolvió una respuesta válida. Por favor, intente más tarde.";
                            break;
                        case "RESOURCE_NOT_FOUND":
                            message = "El servicio de búsqueda de trabajos no está disponible en este momento. Por favor, intente más tarde.";
                            break;
                        case "RATE_LIMIT_EXCEEDED":
                            message = "Se ha excedido el límite de solicitudes al servicio. Por favor, espere unos minutos e intente nuevamente.";
                            break;
                        case "PROCESSING_ERROR":
                            message = "Ocurrió un error al procesar su solicitud. Por favor, intente más tarde.";
                            break;
                    }
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<List<JobEntity>>error(message)));
                }
                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<List<JobEntity>>error(
                    "Ocurrió un error al procesar la búsqueda. Por favor, intente más tarde.")));
            });
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<JobEntity>>>> getAllJobs() {
        return jobSearchService.getAllJobs()
            .collectList()
            .map(jobs -> {
                ApiResponse<List<JobEntity>> response = ApiResponse.success(jobs, "Lista de trabajos obtenida correctamente");
                response.setHasData(!jobs.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                System.err.println("Error al obtener todos los trabajos. Error: " + error.getMessage());
                if (error instanceof ApiException apiError) {
                    String errorCode = apiError.getErrorCode();
                    String message = apiError.getMessage();
                    switch (errorCode) {
                        case "DATA_FORMAT_ERROR":
                            message = "El formato de la respuesta de la API no es válido. Por favor, intente más tarde.";
                            break;
                        case "INCOMPLETE_DATA":
                            message = "La información de trabajos está incompleta. Por favor, intente más tarde.";
                            break;
                        case "NO_JOBS_FOUND":
                            message = "No hay trabajos disponibles en este momento. Por favor, intente más tarde.";
                            break;
                        case "NULL_RESPONSE":
                            message = "La API no devolvió una respuesta válida. Por favor, intente más tarde.";
                            break;
                        case "RESOURCE_NOT_FOUND":
                            message = "El servicio de búsqueda de trabajos no está disponible en este momento. Por favor, intente más tarde.";
                            break;
                        case "RATE_LIMIT_EXCEEDED":
                            message = "Se ha excedido el límite de solicitudes al servicio. Por favor, espere unos minutos e intente nuevamente.";
                            break;
                        case "PROCESSING_ERROR":
                            message = "Ocurrió un error al procesar su solicitud. Por favor, intente más tarde.";
                            break;
                    }
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<List<JobEntity>>error(message)));
                }
                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<List<JobEntity>>error(
                    "Ocurrió un error al obtener la lista de trabajos. Por favor, intente más tarde.")));
            });
    }
}