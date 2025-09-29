package pe.edu.vallegrande.projectPruebasApis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table("movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity {
    @Id
    private Long id;

    @Column("movie_id")
    private String movieId;

    @Column("title")
    private String title;

    @Column("overview")
    private String overview;

    @Column("poster_path")
    private String posterPath;

    @Column("release_date")
    private String releaseDate;

    @Column("vote_average")
    private Double voteAverage;

    @Column("vote_count")
    private Integer voteCount;

    @Column("popularity")
    private Double popularity;

    @Column("genre_ids")
    private String genreIds;

    @Column("search_date")
    private String searchDate;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;
}