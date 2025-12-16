package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class DiscoverItem {
    private String postId;
    private String title;
    private String coverPhoto;
    private long createdAt;

    private List<String> tags;

    private String userId;
    private String userName;
    private String userAvatar;

    private long likesCount;
    private long commentsCount;
}
