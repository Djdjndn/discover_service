package com.datn.discover_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPost {

    @Id
    @Column(name = "post_id", length = 36)
    private String postId;

    @Column(name = "trip_id", nullable = false)
    private String tripId;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_avatar")
    private String ownerAvatar;

    private String title;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    private String thumbnail;

    @Column(columnDefinition = "JSON")
    private String images;

    @Column(columnDefinition = "JSON")
    private String locations;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "comments_count")
    private int commentsCount;

    @Column(name = "created_at")
    private Long createdAt;
}
