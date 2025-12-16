package com.datn.discover_service.model;

import java.util.List;

import lombok.Data;

@Data
public class PostData {
    private String postId;

    private String userId;
    private String userName;
    private String userAvatar;

    private String title;
    private String content;

    private List<String> images;
    private List<String> tags;

    private String tripId;
    private String locationName;

    private long createdAt;

    private int likesCount;
    private int commentsCount;

    private boolean isPublic = true;
}
