package pe.edu.vallegrande.projectPruebasApis.model.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchResponse {
    private List<MovieData> movies;
    private Metadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieData {
        private Boolean adult;
        private String backdrop_path;
        private List<Integer> genre_ids;
        private String id;
        private String original_language;
        private String original_title;
        private String overview;
        private Double popularity;
        private String poster_path;
        private String release_date;
        private String title;
        private Boolean video;
        private Double vote_average;
        private Integer vote_count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private String requestId;
        private String timestamp;
        private String source;
        private String query;
        private String sanitized_query;
    }
}