package com.datn.discover_service.dto;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class DiscoverItem {
    private String tripId;
    private String userId;
    private String userName;
    private String userAvatar;

    private String tripImage;
    private String caption;
    private String tags;
    private boolean isFollowing;
    private String isPublic;
    private LocalDateTime sharedAt;
}
