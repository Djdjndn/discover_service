package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreatePostRequest {

    private String tripId;              // trip thật
    private String shortDescription;    // người dùng nhập
    private String thumbnail;           // URL ảnh
    private List<String> images;        // danh sách URL
}
