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

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobSearchController {

    private final JobSearchService jobSearchService;

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