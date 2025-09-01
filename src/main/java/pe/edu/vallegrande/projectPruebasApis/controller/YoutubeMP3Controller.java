package pe.edu.vallegrande.projectPruebasApis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.projectPruebasApis.entity.YoutubeMP3Entity;
import pe.edu.vallegrande.projectPruebasApis.exception.ApiException;
import pe.edu.vallegrande.projectPruebasApis.model.ApiResponse;
import pe.edu.vallegrande.projectPruebasApis.service.YoutubeMP3Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "youtube.api.enabled", havingValue = "true", matchIfMissing = false)
public class YoutubeMP3Controller {

    private final YoutubeMP3Service youtubeMP3Service;

    @GetMapping("/download/{videoId}")
    public Mono<ResponseEntity<ApiResponse<YoutubeMP3Entity>>> getYoutubeMP3Info(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "low") String quality) {
        // Validar el videoId antes de procesar
        if (videoId == null || videoId.trim().isEmpty()) {
            return Mono.error(new ApiException(
                "El ID del video no puede estar vacío",
                "YoutubeMP3",
                "INVALID_VIDEO_ID"
            ));
        }
        
        // Validar la calidad
        if (!quality.equals("low") && !quality.equals("medium") && !quality.equals("high")) {
            return Mono.error(new ApiException(
                "La calidad debe ser 'low', 'medium' o 'high'",
                "YoutubeMP3",
                "INVALID_QUALITY"
            ));
        }
        
        return youtubeMP3Service.getYoutubeMP3Info(videoId, quality)
                .map(entity -> ResponseEntity.ok(ApiResponse.success(entity, "Información de MP3 obtenida correctamente")))
                .onErrorResume(error -> {
                    // Registrar el error para depuración
                    System.err.println("Error al procesar la solicitud para el video ID: " + videoId + ". Error: " + error.getMessage());
                    
                    // Proporcionar un mensaje más detallado según el tipo de error
                    if (error instanceof ApiException) {
                        ApiException apiError = (ApiException) error;
                        String errorCode = apiError.getErrorCode();
                        String message = apiError.getMessage();
                        
                        // Personalizar mensajes según el código de error
                        switch (errorCode) {
                            case "DATA_FORMAT_ERROR":
                                message = "El formato de la respuesta de la API no es válido. Por favor, intente con otro ID de video.";
                                break;
                            case "INCOMPLETE_DATA":
                                message = "La información del video está incompleta. Por favor, intente con otro ID de video.";
                                break;
                            case "DATA_NOT_FOUND":
                                message = "No se encontró información para este video. Verifique que el ID sea correcto.";
                                break;
                            case "NULL_RESPONSE":
                                message = "La API no devolvió una respuesta válida. Por favor, intente más tarde.";
                                break;
                        }
                        
                        return Mono.just(ResponseEntity.badRequest().body(ApiResponse.error(message)));
                    }
                    
                    // Para otros errores, devolver un mensaje genérico
                    return Mono.just(ResponseEntity.badRequest().body(ApiResponse.error(
                        "Ocurrió un error al procesar la solicitud. Por favor, intente más tarde.")));
                });
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Flux<YoutubeMP3Entity>>>> getAllYoutubeMP3() {
        return Mono.just(ResponseEntity.ok(ApiResponse.success(youtubeMP3Service.getAllYoutubeMP3(), "Lista de MP3 obtenida correctamente")));
    }
    
    @GetMapping("/download-file/{videoId}")
    public Mono<Void> downloadMP3File(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "low") String quality,
            ServerHttpResponse response) {
        
        // Validar el videoId antes de procesar
        if (videoId == null || videoId.trim().isEmpty()) {
            return Mono.error(new ApiException(
                "El ID del video no puede estar vacío",
                "YoutubeMP3",
                "INVALID_VIDEO_ID"
            ));
        }
        
        // Validar la calidad
        if (!quality.equals("low") && !quality.equals("medium") && !quality.equals("high")) {
            return Mono.error(new ApiException(
                "La calidad debe ser 'low', 'medium' o 'high'",
                "YoutubeMP3",
                "INVALID_QUALITY"
            ));
        }
        
        // Primero guardar la información en la base de datos
        return youtubeMP3Service.getYoutubeMP3Info(videoId, quality)
                .doOnSuccess(entity -> {
                    System.out.println("Información guardada correctamente en la base de datos: " + entity);
                })
                .doOnError(error -> {
                    System.err.println("Error al guardar en la base de datos: " + error.getMessage());
                })
                .flatMap(entity -> {
                    // Configurar los headers para la descarga
                    response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"youtube_" + videoId + ".mp3\"");
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    
                    // Retornar el flujo de datos directamente al cliente
                    return response.writeWith(youtubeMP3Service.downloadMP3(videoId, quality));
                })
                .onErrorResume(error -> {
                    System.err.println("Error al descargar el archivo MP3: " + error.getMessage());
                    return Mono.error(error);
                });
    }
}