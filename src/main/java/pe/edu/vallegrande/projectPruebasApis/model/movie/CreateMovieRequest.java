package pe.edu.vallegrande.projectPruebasApis.model.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {
    
    @NotBlank(message = "Movie ID cannot be blank")
    private String movieId;
    
    @NotBlank(message = "Title cannot be blank")
    private String title;
    
    private String overview;
    
    private String posterPath;
    
    private String releaseDate;
    
    @DecimalMin(value = "0.0", message = "Vote average must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Vote average must be at most 10.0")
    private Double voteAverage;
    
    @Min(value = 0, message = "Vote count must be at least 0")
    private Integer voteCount;
    
    @Min(value = 0, message = "Popularity must be at least 0")
    private Double popularity;
    
    private String genreIds;
}