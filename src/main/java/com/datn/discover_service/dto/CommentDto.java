package com.datn.discover_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private String planId;
    private String userId;
    private String userName;
    private String userAvatar;
    private String parentId; // For replies
    private String content;
    private Long createdAt; // Timestamp in milliseconds
}
