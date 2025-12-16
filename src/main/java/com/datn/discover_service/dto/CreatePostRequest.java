package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String userId;
    private String title;
    private String content;

    private List<String> images;
    private List<String> tags;

    private String tripId;
}
