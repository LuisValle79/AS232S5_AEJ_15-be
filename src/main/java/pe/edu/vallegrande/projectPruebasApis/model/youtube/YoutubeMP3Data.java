package pe.edu.vallegrande.projectPruebasApis.model.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeMP3Data {
    private String title;
    private String link;
    private String videoId;
    private String quality;
    private String thumbnail;
    private String duration;
}