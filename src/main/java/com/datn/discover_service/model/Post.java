package com.datn.discover_service.model;

import java.util.List;

import lombok.Data;

@Data
public class Post {

    private String postId;        // docId Firestore
    private String userId;        // ID người đăng
    private String tripId;        // liên kết sang trip
    
    private String title;
    private String content;

    private List<String> images;  // danh sách ảnh
    private String coverPhoto;

    private List<String> tags;

    private Long createdAt;       // timestamp

    private Boolean isPublic;

    private Integer likesCount;      // tổng số like (nếu bạn có field này)
    private Integer commentsCount;   // tổng số comment (nếu bạn có field này)
}
