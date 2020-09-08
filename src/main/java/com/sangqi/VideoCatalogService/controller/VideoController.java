package com.sangqi.VideoCatalogService.controller;

import com.google.common.collect.Lists;
import com.sangqi.VideoCatalogService.repository.model.Video;
import com.sangqi.VideoCatalogService.service.VideoMetadataService;
import com.sangqi.VideoCatalogService.service.VideoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Controller
public class VideoController {

    private final VideoMetadataService videoMetadataService;
    private final VideoStorageService videoStorageService;

    @Autowired
    VideoController(
        VideoMetadataService videoMetadataService,
        VideoStorageService videoStorageService
    ) {
        this.videoMetadataService = videoMetadataService;
        this.videoStorageService = videoStorageService;
    }

    @PostMapping("/video")
    @ResponseBody
    public Video addVideo(@RequestBody Video video, HttpServletResponse response) {
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        try {
            return videoMetadataService.saveVideo(video);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @GetMapping("/video")
    @ResponseBody
    public List<Video> getAllVideos() {
        return videoMetadataService.getAllVideos();
    }

    @GetMapping("/video/{id}")
    @ResponseBody
    public Video getVideoById(@PathVariable("id") String videoId, HttpServletResponse response) {
        Video video = getVideoById(videoId);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return video;
    }

    @GetMapping("/video/search/findByTitle")
    @ResponseBody
    public List<Video> getVideosByTitle(@RequestParam("title") String title, HttpServletResponse response) {
        if (title == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Lists.newArrayList();
        }

        return videoMetadataService.getVideosByTitle(title);
    }

    @GetMapping("/video/search/findByDurationLessThan")
    @ResponseBody
    public List<Video> getVideosByDurationLessThan(
        @RequestParam("duration") Long durationMillis,
        HttpServletResponse response
    ) {
        if (durationMillis == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Lists.newArrayList();
        }

        return videoMetadataService.getVideosDurationLessThan(durationMillis);
    }

//    @PostMapping("/video/{id}/like")
//    @ResponseBody
//    public void likeVideo(@PathVariable("id") Long videoId, HttpServletResponse response) {
//        Video video = getVideoById(videoId);
//        if (video == null) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//
//        video.setLikes(video.getLikes() + 1);
//        videoService.saveVideo(video);
//    }
//
//    @PostMapping("/video/{id}/unlike")
//    @ResponseBody
//    public void unlikeVideo(@PathVariable("id") Long videoId, HttpServletResponse response) {
//        Video video = getVideoById(videoId);
//        if (video == null) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//
//        if (video.getLikes() == 0) {
//            return;
//        }
//        video.setLikes(video.getLikes() - 1);
//        videoService.saveVideo(video);
//    }

    @PostMapping("/video/{id}/data")
    @ResponseBody
    public Video addVideoData(
        @PathVariable("id") String videoId,
        @RequestPart(value = "data") MultipartFile videoData,
        HttpServletResponse response
    ) {
        Video video = getVideoById(videoId);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (videoData == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        try {
            String url = videoStorageService.uploadVideoFile(video, videoData);
            video.setUrl(url);
            return video;
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] getVideoData(@PathVariable("id") String videoId, HttpServletResponse response) {
        Video video = getVideoById(videoId);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        try {
            return videoStorageService.downloadVideoFile(video);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @Nullable
    private Video getVideoById(String videoId) {
        Optional<Video> videoOptional = videoMetadataService.getVideoById(videoId);
        return videoOptional.orElse(null);
    }
}
