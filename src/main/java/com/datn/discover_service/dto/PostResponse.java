package com.datn.discover_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private String postId;
    private String caption;
    private boolean isPublic;
    private long createdAt;

    private String userId;
    private String userName;
    private String userAvatar;

    private String tripId;
    private String tripName;
    private String tripImage;

    private long likeCount;       // 🔥 đổi int → long
    private long commentCount;    // 🔥 đổi int → long
    private boolean isLiked;
}
