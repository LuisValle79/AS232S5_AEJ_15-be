package pe.edu.vallegrande.projectPruebasApis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.projectPruebasApis.entity.MovieEntity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.ApiResponse;
import pe.edu.vallegrande.projectPruebasApis.service.MovieService;
import reactor.core.publisher.Flux;
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
        
        // Validar el título antes de procesar
        if (title == null || title.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<List<MovieEntity>>error("El título de la película no puede estar vacío")
            ));
        }
        
        // Obtener las películas y devolverlas en la respuesta
        return movieService.searchMovies(title)
            .collectList()
            .map(movies -> {
                ApiResponse<List<MovieEntity>> response = ApiResponse.success(movies, "Búsqueda de películas realizada correctamente");
                response.setHasData(!movies.isEmpty());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                // Registrar el error para depuración
                System.err.println("Error al buscar películas para: " + title + ". Error: " + error.getMessage());
                
                // Proporcionar un mensaje más detallado según el tipo de error
                if (error instanceof ApiException) {
                    ApiException apiEx = (ApiException) error;
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<List<MovieEntity>>error(apiEx.getMessage())
                    ));
                } else {
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<List<MovieEntity>>error("Error al buscar películas: " + error.getMessage())
                    ));
                }
            });
    }
    
    @GetMapping("/getID")
    public Mono<ResponseEntity<ApiResponse<MovieEntity>>> getMovieById(
            @RequestParam String title) {
        
        // Validar el título antes de procesar
        if (title == null || title.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(
                ApiResponse.<MovieEntity>error("El título de la película no puede estar vacío")
            ));
        }
        
        // Obtener la película por ID y devolverla en la respuesta
        return movieService.getMovieById(title)
            .map(movie -> {
                ApiResponse<MovieEntity> response = ApiResponse.success(movie, "Película encontrada correctamente");
                response.setHasData(movie != null);
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                // Registrar el error para depuración
                System.err.println("Error al obtener la película: " + title + ". Error: " + error.getMessage());
                
                // Proporcionar un mensaje más detallado según el tipo de error
                if (error instanceof ApiException) {
                    ApiException apiEx = (ApiException) error;
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<MovieEntity>error(apiEx.getMessage())
                    ));
                } else {
                    return Mono.just(ResponseEntity.badRequest().body(
                        ApiResponse.<MovieEntity>error("Error al obtener la película: " + error.getMessage())
                    ));
                }
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
                return Mono.just(ResponseEntity.badRequest().body(
                    ApiResponse.<List<MovieEntity>>error("Error al obtener las películas: " + error.getMessage())
                ));
            });
    }
}