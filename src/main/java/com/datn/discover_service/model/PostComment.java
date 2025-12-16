package com.datn.discover_service.model;

import lombok.Data;

@Data
public class PostComment {
    private String id;        // documentId
    private String postId;
    private String userId;
    private String content;
    private long createdAt;

    // optional
    private String userName;
    private String avatar;
}
