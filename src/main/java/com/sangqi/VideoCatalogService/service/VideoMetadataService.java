package com.sangqi.VideoCatalogService.service;

import com.google.common.collect.Lists;
import com.sangqi.VideoCatalogService.repository.VideoRepository;
import com.sangqi.VideoCatalogService.repository.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VideoMetadataService {

    private final VideoRepository videoRepository;

    @Autowired
    VideoMetadataService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Video saveVideo(Video video) {
        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return Lists.newArrayList(videoRepository.findAll());
    }

    public Optional<Video> getVideoById(String videoId) {
        return videoRepository.findById(videoId);
    }

    public List<Video> getVideosByTitle(String title) {
        return videoRepository.findByTitle(title);
    }

    public List<Video> getVideosDurationLessThan(Long durationMillis) {
        return videoRepository.findByDurationMillisLessThan(durationMillis);
    }
}
