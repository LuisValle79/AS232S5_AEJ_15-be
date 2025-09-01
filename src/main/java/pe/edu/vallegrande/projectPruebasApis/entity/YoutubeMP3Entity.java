package pe.edu.vallegrande.projectPruebasApis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("youtube_mp3")
public class YoutubeMP3Entity {
    @Id
    private Long id;
    
    @Column("title")
    private String title;
    
    @Column("link")
    private String link;
    
    @Column("video_id")
    private String videoId;
    
    @Column("quality")
    private String quality;
    
    @Column("thumbnail")
    private String thumbnail;
    
    @Column("duration")
    private String duration;
    
    @Column("search_date")
    private String searchDate;
    
    public YoutubeMP3Entity() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getVideoId() {
        return videoId;
    }
    
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    
    public String getQuality() {
        return quality;
    }
    
    public void setQuality(String quality) {
        this.quality = quality;
    }
    
    public String getThumbnail() {
        return thumbnail;
    }
    
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getSearchDate() {
        return searchDate;
    }
    
    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }
    
    @Override
    public String toString() {
        return "YoutubeMP3Entity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", videoId='" + videoId + '\'' +
                ", quality='" + quality + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", duration='" + duration + '\'' +
                ", searchDate='" + searchDate + '\'' +
                '}';
    }
}