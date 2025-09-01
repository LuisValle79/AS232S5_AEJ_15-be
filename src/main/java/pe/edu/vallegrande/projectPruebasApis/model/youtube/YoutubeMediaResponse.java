package pe.edu.vallegrande.projectPruebasApis.model.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeMediaResponse {
    private String id;
    private String title;
    private String description;
    private String channelTitle;
    private String channelId;
    private String channelUrl;
    private String uploadDate;
    private String publishDate;
    private String viewCount;
    private String likeCount;
    private String commentCount;
    private String duration;
    private String thumbnails;
    private String[] tags;
    private String category;
    private String[] formats;
    private String[] adaptiveFormats;
    private String[] relatedVideos;
}