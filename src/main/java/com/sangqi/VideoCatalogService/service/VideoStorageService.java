package com.sangqi.VideoCatalogService.service;

import com.sangqi.VideoCatalogService.repository.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class VideoStorageService {

    private static final String VIDEO_FILE_KEY = "video_%s";

    private final AmazonClient amazonClient;

    @Autowired
    VideoStorageService(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    public String uploadVideoFile(Video video, MultipartFile multipartFile) throws Exception {
        String fileKey = getFileKey(video);
        return amazonClient.uploadFile(fileKey, multipartFile);
    }

    public byte[] downloadVideoFile(Video video) throws Exception {
        String fileKey = getFileKey(video);
        return amazonClient.downloadFile(fileKey).toByteArray();
    }

    private String getFileKey(Video video) {
        return String.format(VIDEO_FILE_KEY, video.getId());
    }
}
