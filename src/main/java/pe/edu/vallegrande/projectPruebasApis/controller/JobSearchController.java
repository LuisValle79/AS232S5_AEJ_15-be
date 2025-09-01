package pe.edu.vallegrande.projectPruebasApis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.projectPruebasApis.entity.JobEntity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.ApiResponse;
import pe.edu.vallegrande.projectPruebasApis.service.JobSearchService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobSearchController {

    private final JobSearchService jobSearchService;

    @GetMapping("/search")
    public Mono<ResponseEntity<ApiResponse<Flux<JobEntity>>>> searchJobs(
            @RequestParam String query,
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "1") Integer numPages) {
        
        // Validar los parámetros de entrada
        if (query == null || query.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<Flux<JobEntity>>error("El parámetro de búsqueda no puede estar vacío")
            ));
        }
        
        if (page < 1 || numPages < 1) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<Flux<JobEntity>>error("Los parámetros de paginación deben ser mayores a cero")
            ));
        }
        
        // Obtener los trabajos y devolverlos directamente en la respuesta
        Flux<JobEntity> jobsFlux = jobSearchService.searchJobs(query, country, page, numPages);
        return Mono.just(ResponseEntity.ok(
            ApiResponse.success(jobsFlux, "Búsqueda de trabajos realizada correctamente")
        ))
            .onErrorResume(error -> {
                // Registrar el error para depuración
                System.err.println("Error al buscar trabajos para: " + query + ". Error: " + error.getMessage());
                
                // Proporcionar un mensaje más detallado según el tipo de error
                if (error instanceof ApiException) {
                    ApiException apiError = (ApiException) error;
                    String errorCode = apiError.getErrorCode();
                    String message = apiError.getMessage();
                    
                    // Personalizar mensajes según el código de error
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
                    
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<Flux<JobEntity>>error(message)));
                }
                
                // Para otros errores, devolver un mensaje genérico
                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<Flux<JobEntity>>error(
                    "Ocurrió un error al procesar la búsqueda. Por favor, intente más tarde.")));
            });
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Flux<JobEntity>>>> getAllJobs() {
        return jobSearchService.getAllJobs()
            .collectList()
            .map(jobList -> ResponseEntity.ok(
                ApiResponse.success(Flux.fromIterable(jobList), "Lista de trabajos obtenida correctamente")
            ))
            .onErrorResume(error -> {
                // Registrar el error para depuración
                System.err.println("Error al obtener todos los trabajos. Error: " + error.getMessage());
                
                // Proporcionar un mensaje más detallado según el tipo de error
                if (error instanceof ApiException) {
                    ApiException apiError = (ApiException) error;
                    String errorCode = apiError.getErrorCode();
                    String message = apiError.getMessage();
                    
                    // Personalizar mensajes según el código de error
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
                    
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<Flux<JobEntity>>error(message)));
                }
                
                // Para otros errores, devolver un mensaje genérico
                return Mono.just(ResponseEntity.badRequest().body(ApiResponse.<Flux<JobEntity>>error(
                    "Ocurrió un error al obtener la lista de trabajos. Por favor, intente más tarde.")));
            });
    }
}