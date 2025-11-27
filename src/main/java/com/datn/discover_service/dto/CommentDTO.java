package com.datn.discover_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDTO {

    private Long id;
    private String postId;

    private String userId;
    private String username;
    private String avatar;

    private String content;

    private long createdAt;
}
