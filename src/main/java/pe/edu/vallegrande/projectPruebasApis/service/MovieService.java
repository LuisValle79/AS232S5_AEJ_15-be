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

    public Flux<MovieEntity> getAllMovies() {
        return movieRepository.findAll();
    }
}