package com.sangqi.VideoCatalogService.repository;

import com.sangqi.VideoCatalogService.repository.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {

    List<Video> findByTitle(String title);

    List<Video> findByDurationMillisLessThan(Long durationMillis);
}
