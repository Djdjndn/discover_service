package com.datn.discover_service.model;

import lombok.Data;

@Data
public class Comment {

    private String commentId;   // docId Firestore
    private String postId;
    private String userId;

    private String content;
    private Long createdAt;
}
