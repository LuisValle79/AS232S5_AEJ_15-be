package pe.edu.vallegrande.projectPruebasApis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.edu.vallegrande.projectPruebasApis.entity.MovieEntity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.movie.CreateMovieRequest;
import pe.edu.vallegrande.projectPruebasApis.model.movie.UpdateMovieRequest;
import pe.edu.vallegrande.projectPruebasApis.model.movie.MovieSearchResponse;
import pe.edu.vallegrande.projectPruebasApis.repository.MovieRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.movie.host}")
    private String rapidApiMovieHost;

    public Flux<MovieEntity> searchMovies(String title) {
        try {
            // Formatear y codificar la consulta como lenguaje natural
            String naturalLanguageQuery = "movies like " + title;
            String encodedQuery = URLEncoder.encode(naturalLanguageQuery, StandardCharsets.UTF_8);

            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(rapidApiMovieHost)
                            .path("/api/search")
                            .queryParam("q", encodedQuery)
                            .build())
                    .header("x-rapidapi-host", rapidApiMovieHost)
                    .header("x-rapidapi-key", rapidApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(MovieSearchResponse.class)
                    .flatMapMany(response -> {
                        // Verificar si la respuesta es nula o no contiene películas
                        if (response == null || response.getMovies() == null || response.getMovies().isEmpty()) {
                            return Flux.error(new ApiException(
                                "No se encontraron películas para: " + title,
                                "MovieSearch",
                                "NO_MOVIES_FOUND"
                            ));
                        }

                        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        return Flux.fromIterable(response.getMovies())
                                .map(movieData -> MovieEntity.builder()
                                        .movieId(movieData.getId())
                                        .title(movieData.getTitle())
                                        .overview(movieData.getOverview())
                                        .releaseDate(movieData.getRelease_date())
                                        .posterPath(movieData.getPoster_path())
                                        .voteAverage(movieData.getVote_average())
                                        .voteCount(movieData.getVote_count())
                                        .popularity(movieData.getPopularity())
                                        .genreIds(movieData.getGenre_ids() != null
                                            ? movieData.getGenre_ids().stream()
                                                .map(String::valueOf)
                                                .collect(Collectors.joining(","))
                                            : null)
                                        .searchDate(currentDate)
                                        .build())
                                .flatMap(movieRepository::save);
                    })
                    .onErrorResume(error -> {
                        if (error instanceof WebClientResponseException wcError) {
                            String responseBody = wcError.getResponseBodyAsString();
                            System.err.println("Cuerpo de la respuesta de error: " + responseBody);
                            if (wcError.getStatusCode().value() == 400) {
                                return Flux.error(new ApiException(
                                    "Solicitud inválida a la API externa: " + responseBody,
                                    "MovieSearch",
                                    "BAD_REQUEST"
                                ));
                            } else if (wcError.getStatusCode().value() == 401) {
                                return Flux.error(new ApiException(
                                    "Error de autenticación con la API externa. Verifique la clave de RapidAPI.",
                                    "MovieSearch",
                                    "UNAUTHORIZED"
                                ));
                            } else if (wcError.getStatusCode().value() == 429) {
                                return Flux.error(new ApiException(
                                    "Se ha excedido el límite de solicitudes a la API externa. Por favor, intente más tarde.",
                                    "MovieSearch",
                                    "RATE_LIMIT_EXCEEDED"
                                ));
                            } else {
                                return Flux.error(new ApiException(
                                    "Error en la API externa: " + wcError.getStatusCode() + " - " + responseBody,
                                    "MovieSearch",
                                    "API_ERROR_" + wcError.getStatusCode()
                                ));
                            }
                        }
                        return Flux.error(new ApiException(
                            "No se pudo procesar la búsqueda de películas: " + error.getMessage(),
                            "MovieSearch",
                            "PROCESSING_ERROR"
                        ));
                    });
        } catch (Exception e) {
            return Flux.error(new ApiException(
                "Error al codificar la consulta: " + title,
                "MovieSearch",
                "ENCODING_ERROR"
            ));
        }
    }

    public Mono<MovieEntity> getMovieById(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(rapidApiMovieHost)
                            .path("/api/getID")
                            .queryParam("title", encodedTitle)
                            .build())
                    .header("x-rapidapi-host", rapidApiMovieHost)
                    .header("x-rapidapi-key", rapidApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(MovieSearchResponse.class)
                    .flatMap(response -> {
                        if (response == null || response.getMovies() == null || response.getMovies().isEmpty()) {
                            return Mono.error(new ApiException(
                                "No se encontró la película: " + title,
                                "MovieSearch",
                                "MOVIE_NOT_FOUND"
                            ));
                        }
                        MovieSearchResponse.MovieData movieData = response.getMovies().get(0);
                        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        return Mono.just(MovieEntity.builder()
                                .movieId(movieData.getId())
                                .title(movieData.getTitle())
                                .overview(movieData.getOverview())
                                .releaseDate(movieData.getRelease_date())
                                .posterPath(movieData.getPoster_path())
                                .voteAverage(movieData.getVote_average())
                                .voteCount(movieData.getVote_count())
                                .popularity(movieData.getPopularity())
                                .genreIds(movieData.getGenre_ids() != null
                                    ? movieData.getGenre_ids().stream()
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(","))
                                    : null)
                                .searchDate(currentDate)
                                .build())
                                .flatMap(movieRepository::save);
                    })
                    .onErrorResume(error -> {
                        if (error instanceof WebClientResponseException wcError) {
                            String responseBody = wcError.getResponseBodyAsString();
                            System.err.println("Cuerpo de la respuesta de error: " + responseBody);
                            if (wcError.getStatusCode().value() == 400) {
                                return Mono.error(new ApiException(
                                    "Solicitud inválida a la API externa: " + responseBody,
                                    "MovieSearch",
                                    "BAD_REQUEST"
                                ));
                            } else if (wcError.getStatusCode().value() == 401) {
                                return Mono.error(new ApiException(
                                    "Error de autenticación con la API externa. Verifique la clave de RapidAPI.",
                                    "MovieSearch",
                                    "UNAUTHORIZED"
                                ));
                            } else if (wcError.getStatusCode().value() == 429) {
                                return Mono.error(new ApiException(
                                    "Se ha excedido el límite de solicitudes a la API externa. Por favor, intente más tarde.",
                                    "MovieSearch",
                                    "RATE_LIMIT_EXCEEDED"
                                ));
                            } else {
                                return Mono.error(new ApiException(
                                    "Error en la API externa: " + wcError.getStatusCode() + " - " + responseBody,
                                    "MovieSearch",
                                    "API_ERROR_" + wcError.getStatusCode()
                                ));
                            }
                        }
                        return Mono.error(new ApiException(
                            "No se pudo procesar la búsqueda de películas: " + error.getMessage(),
                            "MovieSearch",
                            "PROCESSING_ERROR"
                        ));
                    });
        } catch (Exception e) {
            return Mono.error(new ApiException(
                "Error al codificar el título: " + title,
                "MovieSearch",
                "ENCODING_ERROR"
            ));
        }
    }

    // ============ Métodos CRUD ============
    
    /**
     * Crear una nueva película
     */
    public Mono<MovieEntity> createMovie(CreateMovieRequest request) {
        // Verificar si ya existe una película activa con el mismo movieId
        return movieRepository.countByMovieIdAndActive(request.getMovieId())
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new ApiException(
                            "Ya existe una película activa con el ID: " + request.getMovieId(),
                            "MovieCreate",
                            "DUPLICATE_MOVIE_ID"
                        ));
                    }
                    
                    String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    MovieEntity movie = MovieEntity.builder()
                            .movieId(request.getMovieId())
                            .title(request.getTitle())
                            .overview(request.getOverview())
                            .posterPath(request.getPosterPath())
                            .releaseDate(request.getReleaseDate())
                            .voteAverage(request.getVoteAverage())
                            .voteCount(request.getVoteCount())
                            .popularity(request.getPopularity())
                            .genreIds(request.getGenreIds())
                            .searchDate(currentDate)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .isActive(true)
                            .build();
                    
                    return movieRepository.save(movie);
                });
    }
    
    /**
     * Obtener película por ID (solo activas)
     */
    public Mono<MovieEntity> findById(Long id) {
        return movieRepository.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Película no encontrada con ID: " + id,
                    "MovieFind",
                    "MOVIE_NOT_FOUND"
                )));
    }
    
    /**
     * Actualizar película
     */
    public Mono<MovieEntity> updateMovie(Long id, UpdateMovieRequest request) {
        return movieRepository.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Película no encontrada con ID: " + id,
                    "MovieUpdate",
                    "MOVIE_NOT_FOUND"
                )))
                .map(existingMovie -> {
                    existingMovie.setTitle(request.getTitle());
                    existingMovie.setOverview(request.getOverview());
                    existingMovie.setPosterPath(request.getPosterPath());
                    existingMovie.setReleaseDate(request.getReleaseDate());
                    existingMovie.setVoteAverage(request.getVoteAverage());
                    existingMovie.setVoteCount(request.getVoteCount());
                    existingMovie.setPopularity(request.getPopularity());
                    existingMovie.setGenreIds(request.getGenreIds());
                    existingMovie.setUpdatedAt(LocalDateTime.now());
                    return existingMovie;
                })
                .flatMap(movieRepository::save);
    }
    
    /**
     * Eliminado lógico de película
     */
    public Mono<Void> deleteMovie(Long id) {
        return movieRepository.findByIdAndActive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Película no encontrada con ID: " + id,
                    "MovieDelete",
                    "MOVIE_NOT_FOUND"
                )))
                .flatMap(movie -> movieRepository.logicalDelete(id));
    }
    
    /**
     * Restaurar película eliminada lógicamente
     */
    public Mono<Void> restoreMovie(Long id) {
        return movieRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Película no encontrada con ID: " + id,
                    "MovieRestore",
                    "MOVIE_NOT_FOUND"
                )))
                .flatMap(movie -> {
                    if (movie.getIsActive()) {
                        return Mono.error(new ApiException(
                            "La película ya está activa",
                            "MovieRestore",
                            "MOVIE_ALREADY_ACTIVE"
                        ));
                    }
                    return movieRepository.restore(id);
                });
    }
    
    /**
     * Buscar películas por título (solo activas)
     */
    public Flux<MovieEntity> findByTitleContaining(String title) {
        return movieRepository.findByTitleContainingIgnoreCaseAndActive(title);
    }
    
    /**
     * Obtener todas las películas activas
     */
    public Flux<MovieEntity> getAllMovies() {
        return movieRepository.findAllActive();
    }
    
    /**
     * Obtener todas las películas eliminadas lógicamente
     */
    public Flux<MovieEntity> getAllDeletedMovies() {
        return movieRepository.findAllInactive();
    }
    
    /**
     * Obtener película eliminada por ID
     */
    public Mono<MovieEntity> findDeletedById(Long id) {
        return movieRepository.findByIdAndInactive(id)
                .switchIfEmpty(Mono.error(new ApiException(
                    "Película eliminada no encontrada con ID: " + id,
                    "MovieFind",
                    "DELETED_MOVIE_NOT_FOUND"
                )));
    }
}