package pe.edu.vallegrande.projectPruebasApis.model.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private String id;
    private String title;
    private String overview;
    private String poster_path;
    private String release_date;
    private Double vote_average;
    private Integer vote_count;
    private Double popularity;
    private List<Integer> genre_ids;

}

