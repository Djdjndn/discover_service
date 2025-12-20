package com.datn.discover_service.dto;

import lombok.Data;

@Data
public class DiscoverItem {

    private String postId;

    // ===== USER =====
    private String userId;
    private String userName;
    private String userAvatar;   // ⭐ avatar user

    // ===== POST =====
    private String caption;      // hoặc title / content
    private Long likesCount;
    private Long commentsCount;

    // ===== TRIP =====
    private String tripImage;    // ⭐ ẢNH TRIP (QUAN TRỌNG NHẤT)

    private long createdAt;
}

