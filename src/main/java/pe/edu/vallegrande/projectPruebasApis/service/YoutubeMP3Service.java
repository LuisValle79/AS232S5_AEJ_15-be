package pe.edu.vallegrande.projectPruebasApis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pe.edu.vallegrande.projectPruebasApis.entity.YoutubeMP3Entity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.youtube.YoutubeMP3Response;
import pe.edu.vallegrande.projectPruebasApis.model.youtube.YoutubeMediaResponse;
import pe.edu.vallegrande.projectPruebasApis.repository.YoutubeMP3Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "youtube.api.enabled", havingValue = "true", matchIfMissing = false)
public class YoutubeMP3Service {

    private final YoutubeMP3Repository youtubeMP3Repository;
    private final WebClient.Builder webClientBuilder;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.youtube.host}")
    private String rapidApiYoutubeHost;

    public Mono<YoutubeMP3Entity> getYoutubeMP3Info(String videoId, String quality) {
        return webClientBuilder.build()
                .get()
                .uri("https://youtube-media-downloader.p.rapidapi.com/v2/video/details?videoId=" + videoId + "&urlAccess=normal&videos=auto&audios=auto")
                .header("x-rapidapi-host", rapidApiYoutubeHost)
                .header("x-rapidapi-key", rapidApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(YoutubeMediaResponse.class)
                .flatMap(response -> {
                    // Verificar si la respuesta contiene datos válidos
                    if (response == null) {
                        return Mono.error(new ApiException(
                            "No se encontraron datos para el video ID: " + videoId,
                            "YoutubeMP3",
                            "DATA_NOT_FOUND"
                        ));
                    }
                    
                    // Construir la entidad con los datos de la nueva API usando setters
                    YoutubeMP3Entity entity = new YoutubeMP3Entity();
                    entity.setTitle("Video de YouTube: " + videoId);
                    entity.setLink("https://www.youtube.com/watch?v=" + videoId);
                    entity.setVideoId(videoId);
                    entity.setQuality(quality);
                    entity.setThumbnail("https://img.youtube.com/vi/" + videoId + "/default.jpg");
                    entity.setDuration("Unknown");
                    entity.setSearchDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    System.out.println("Intentando guardar en la base de datos: " + entity);
                    return youtubeMP3Repository.save(entity)
                            .doOnSuccess(savedEntity -> {
                                System.out.println("Entidad guardada correctamente: " + savedEntity);
                            })
                            .doOnError(error -> {
                                System.err.println("Error al guardar la entidad: " + error.getMessage());
                                error.printStackTrace();
                            });
                })
                .onErrorResume(error -> {
                    // Manejar errores específicos
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException wcError = (WebClientResponseException) error;
                        // No tratar el código 200 OK como un error
                        if (wcError.getStatusCode().is2xxSuccessful()) {
                            System.err.println("Respuesta 2xx recibida pero con error: " + wcError.getMessage());
                            return Mono.error(new ApiException(
                                "La API devolvió una respuesta exitosa pero con formato incorrecto. Por favor, inténtelo de nuevo más tarde.",
                                "YoutubeMP3",
                                "DATA_FORMAT_ERROR"
                            ));
                        } else {
                            return Mono.error(new ApiException(
                                "Error en la API externa: " + wcError.getStatusCode() + " - " + wcError.getStatusText(),
                                "YoutubeMP3",
                                "API_ERROR_" + wcError.getStatusCode()
                            ));
                        }
                    } else if (error instanceof ApiException) {
                        // Pasar la ApiException tal como está
                        return Mono.error(error);
                    }
                    
                    // Manejar otros errores
                    System.err.println("Error al procesar la solicitud para el video ID: " + videoId + ". Error: " + error.getMessage());
                    return Mono.error(new ApiException(
                        "No se pudo procesar la información del video. Por favor, verifica que el ID sea válido.",
                        "YoutubeMP3",
                        "PROCESSING_ERROR"
                    ));
                });
    }

    public Flux<YoutubeMP3Entity> getAllYoutubeMP3() {
        return youtubeMP3Repository.findAll();
    }
    
    public Flux<DataBuffer> downloadMP3(String videoId, String quality) {
        System.out.println("Intentando descargar MP3 para videoId: " + videoId + " con calidad: " + quality);
        
        // Usamos la nueva API para descargar el audio
        String downloadUrl = "https://youtube-media-downloader.p.rapidapi.com/v2/video/download?videoId=" + videoId + "&quality=" + quality;
        System.out.println("URL de descarga: " + downloadUrl);
        
        return webClientBuilder.build()
                .get()
                .uri(downloadUrl)
                .header("x-rapidapi-host", rapidApiYoutubeHost)
                .header("x-rapidapi-key", rapidApiKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        System.out.println("Respuesta exitosa de la API, código: " + response.statusCode());
                        return response.body(BodyExtractors.toDataBuffers())
                                .doOnNext(buffer -> System.out.println("Recibido buffer de datos de tamaño: " + buffer.readableByteCount()))
                                .switchIfEmpty(Flux.error(new ApiException(
                                    "La API devolvió una respuesta exitosa pero sin datos.",
                                    "YoutubeMP3",
                                    "EMPTY_RESPONSE"
                                )));
                    } else {
                        System.err.println("Error en la API, código: " + response.statusCode());
                        return Flux.error(new ApiException(
                            "Error en la API externa: " + response.statusCode(),
                            "YoutubeMP3",
                            "API_ERROR_" + response.statusCode().value()
                        ));
                    }
                })
                .onErrorResume(error -> {
                    System.err.println("Error al descargar MP3: " + error.getMessage());
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException wcError = (WebClientResponseException) error;
                        return Flux.error(new ApiException(
                            "Error en la API externa: " + wcError.getStatusCode() + " - " + wcError.getStatusText(),
                            "YoutubeMP3",
                            "API_ERROR_" + wcError.getStatusCode()
                        ));
                    }
                    return Flux.error(new ApiException(
                        "No se pudo descargar el archivo MP3. Por favor, verifica que el ID sea válido.",
                        "YoutubeMP3",
                        "DOWNLOAD_ERROR"
                    ));
                });
    }
}