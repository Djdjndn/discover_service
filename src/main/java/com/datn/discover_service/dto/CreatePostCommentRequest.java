package com.datn.discover_service.dto;

import lombok.Data;

@Data
public class CreatePostCommentRequest {
    private String userId;
    private String userName;
    private String avatar;
    private String content;
}
