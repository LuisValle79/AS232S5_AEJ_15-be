package pe.edu.vallegrande.projectPruebasApis.model.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeMP3Response {
    private String status;
    private String message;
    private YoutubeMP3Data data;
}