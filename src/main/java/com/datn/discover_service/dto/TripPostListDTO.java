package com.datn.discover_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripPostListDTO {

    private String postId;
    private String tripId;

    private String ownerName;
    private String ownerAvatar;

    private String title;
    private String thumbnail;

    private List<String> locations;

    private int likesCount;
    private int commentsCount;

    private long createdAt;
}
