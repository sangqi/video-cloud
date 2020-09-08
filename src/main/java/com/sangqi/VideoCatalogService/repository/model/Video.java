package com.sangqi.VideoCatalogService.repository.model;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

/**
 * Simple data model representing a Video object which contains title, url, duration, etc
 */
@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String title;
    private String url;
    private String mimeType;
    private long durationMillis;
    private long likes;

    @ElementCollection
    private Set<String> likedBy = Sets.newHashSet();

    private Video() {
    }

    public Video(String title, String url, String mimeType, Long durationMillis, Long likes) {
        this.title = title;
        this.url = url;
        this.mimeType = mimeType;
        this.durationMillis = durationMillis;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public Set<String> getLikedBy() {
        return likedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equal(title, video.title) &&
            Objects.equal(url, video.url) &&
            Objects.equal(durationMillis, video.durationMillis);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title, url, durationMillis);
    }
}
