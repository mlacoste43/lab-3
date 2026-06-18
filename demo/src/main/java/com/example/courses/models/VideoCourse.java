package com.example.courses.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class VideoCourse extends OnlineCourse {

    private String resolution = "720p";

    private boolean subtitles;

     private static final String[] resolutions = {"480p", "720p", "1080p"};

    public void toggleSubtitles() {
        subtitles = !subtitles;
    }

    public void nextResolution() {
        int index = java.util.Arrays.asList(resolutions).indexOf(resolution);
        index = (index + 1) % resolutions.length;
        resolution = resolutions[index];
    }

    @Override
    public String getInfo() {
        return super.getInfo() +
                ", Тип: Видео курс";
    }
}