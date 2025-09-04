package pe.edu.vallegrande.projectPruebasApis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.projectPruebasApis.entity.MovieEntity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.ApiResponse;
import pe.edu.vallegrande.projectPruebasApis.service.MovieService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/search")
    public Mono<ResponseEntity<ApiResponse<List<MovieEntity>>>> searchMovies(
            @RequestParam String title) {
        
        if (title == null || title.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<MovieEntity>>error("El título de la película no puede estar vacío")
            ));
        }
        
        return movieService.searchMovies(title)
            .collectList()
            .map(movies -> {
                ApiResponse<List<MovieEntity>> response = ApiResponse.success(movies, "Búsqueda de películas realizada correctamente");
                response.setHasData(!movies.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                System.err.println("Error al buscar películas para: " + title + ". Error: " + error.getMessage());
                if (error instanceof ApiException apiEx) {
                    String errorCode = apiEx.getErrorCode();
                    String message = apiEx.getMessage();
                    switch (errorCode) {
                        case "NO_MOVIES_FOUND":
                            message = "No se encontraron películas para la búsqueda: " + title;
                            break;
                        case "BAD_REQUEST":
                            message = "Solicitud inválida: " + apiEx.getMessage();
                            break;
                        case "UNAUTHORIZED":
                            message = "Error de autenticación con la API externa. Por favor, contacte al administrador.";
                            break;
                        case "RATE_LIMIT_EXCEEDED":
                            message = "Se ha excedido el límite de solicitudes al servicio. Por favor, espere unos minutos e intente nuevamente.";
                            break;
                        case "PROCESSING_ERROR":
                            message = "Ocurrió un error al procesar la búsqueda de películas. Por favor, intente más tarde.";
                            break;
                    }
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<List<MovieEntity>>error(message)
                    ));
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<MovieEntity>>error("Error al buscar películas: " + error.getMessage())
                ));
            });
    }

    @GetMapping("/getID")
    public Mono<ResponseEntity<ApiResponse<MovieEntity>>> getMovieById(
            @RequestParam String title) {
        
        if (title == null || title.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<MovieEntity>error("El título de la película no puede estar vacío")
            ));
        }
        
        return movieService.getMovieById(title)
            .map(movie -> {
                ApiResponse<MovieEntity> response = ApiResponse.success(movie, "Película encontrada correctamente");
                response.setHasData(movie != null);
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                System.err.println("Error al obtener la película: " + title + ". Error: " + error.getMessage());
                if (error instanceof ApiException apiEx) {
                    String errorCode = apiEx.getErrorCode();
                    String message = apiEx.getMessage();
                    switch (errorCode) {
                        case "MOVIE_NOT_FOUND":
                            message = "No se encontró la película: " + title;
                            break;
                        case "BAD_REQUEST":
                            message = "Solicitud inválida: " + apiEx.getMessage();
                            break;
                        case "UNAUTHORIZED":
                            message = "Error de autenticación con la API externa. Por favor, contacte al administrador.";
                            break;
                        case "RATE_LIMIT_EXCEEDED":
                            message = "Se ha excedido el límite de solicitudes al servicio. Por favor, espere unos minutos e intente nuevamente.";
                            break;
                        case "PROCESSING_ERROR":
                            message = "Ocurrió un error al procesar la búsqueda de películas. Por favor, intente más tarde.";
                            break;
                    }
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<MovieEntity>error(message)
                    ));
                }
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<MovieEntity>error("Error al obtener la película: " + error.getMessage())
                ));
            });
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<MovieEntity>>>> getAllMovies() {
        return movieService.getAllMovies()
            .collectList()
            .map(movies -> {
                ApiResponse<List<MovieEntity>> response = ApiResponse.success(movies, "Películas obtenidas correctamente");
                response.setHasData(!movies.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                System.err.println("Error al obtener todas las películas: " + error.getMessage());
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<MovieEntity>>error("Error al obtener las películas: " + error.getMessage())
                ));
            });
    }
}