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
import pe.edu.vallegrande.projectPruebasApis.model.movie.MovieResponse;
import pe.edu.vallegrande.projectPruebasApis.repository.MovieRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    // Buscar películas por título (lista)
    public Flux<MovieEntity> searchMovies(String title) {
        return webClientBuilder.build()
                .get()
                .uri("https://ai-movie-recommender.p.rapidapi.com/api/search?query=" + title)
                .header("x-rapidapi-host", rapidApiMovieHost)
                .header("x-rapidapi-key", rapidApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(MovieResponse.class) // ✅ es un array de películas
                .flatMap(movieData -> {
                    MovieEntity movieEntity = new MovieEntity();
                    movieEntity.setMovieId(movieData.getId());
                    movieEntity.setTitle(movieData.getTitle());
                    movieEntity.setOverview(movieData.getOverview());
                    movieEntity.setPosterPath(movieData.getPoster_path());
                    movieEntity.setReleaseDate(movieData.getRelease_date());
                    movieEntity.setVoteAverage(movieData.getVote_average());
                    movieEntity.setVoteCount(movieData.getVote_count());
                    movieEntity.setPopularity(movieData.getPopularity());

                    if (movieData.getGenre_ids() != null) {
                        String genreIds = movieData.getGenre_ids().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        movieEntity.setGenreIds(genreIds);
                    }

                    movieEntity.setSearchDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                    return movieRepository.save(movieEntity);
                })
                .switchIfEmpty(Flux.error(new ApiException(
                        "No se encontraron películas para: " + title,
                        "Movie",
                        "NO_MOVIES_FOUND"
                )))
                .onErrorResume(WebClientResponseException.class, e -> {
                    return Flux.error(new ApiException(
                            "Error al buscar películas: " + e.getMessage(),
                            "Movie",
                            "API_ERROR"
                    ));
                });
    }

    // Buscar una película por título (objeto único)
    public Mono<MovieEntity> getMovieById(String title) {
        return webClientBuilder.build()
                .get()
                .uri("https://ai-movie-recommender.p.rapidapi.com/api/getID?title=" + title)
                .header("x-rapidapi-host", rapidApiMovieHost)
                .header("x-rapidapi-key", rapidApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(MovieResponse.class) // ✅ es un único objeto
                .flatMap(movieData -> {
                    if (movieData == null) {
                        return Mono.error(new ApiException(
                                "No se encontró la película: " + title,
                                "Movie",
                                "MOVIE_NOT_FOUND"
                        ));
                    }

                    MovieEntity movieEntity = new MovieEntity();
                    movieEntity.setMovieId(movieData.getId());
                    movieEntity.setTitle(movieData.getTitle());
                    movieEntity.setOverview(movieData.getOverview());
                    movieEntity.setPosterPath(movieData.getPoster_path());
                    movieEntity.setReleaseDate(movieData.getRelease_date());
                    movieEntity.setVoteAverage(movieData.getVote_average());
                    movieEntity.setVoteCount(movieData.getVote_count());
                    movieEntity.setPopularity(movieData.getPopularity());

                    if (movieData.getGenre_ids() != null) {
                        String genreIds = movieData.getGenre_ids().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        movieEntity.setGenreIds(genreIds);
                    }

                    movieEntity.setSearchDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                    return movieRepository.save(movieEntity);
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    return Mono.error(new ApiException(
                            "Error al obtener la película: " + e.getMessage(),
                            "Movie",
                            "API_ERROR"
                    ));
                });
    }

    // Listar todas las películas guardadas
    public Flux<MovieEntity> getAllMovies() {
        return movieRepository.findAll();
    }
}
