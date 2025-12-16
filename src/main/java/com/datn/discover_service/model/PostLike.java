package com.datn.discover_service.model;

import lombok.Data;

@Data
public class PostLike {
    private String id;        // documentId
    private String postId;
    private String userId;
    private long createdAt;
}
