package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class PostDetailResponse {

    private PostDto post;
    private UserDto user;
    private TripShortDto trip;
    private LikesInfoDto likes;
    private List<CommentDto> comments;

    @Data
    public static class PostDto {
        private String postId;
        private String title;
        private String content;
        private List<String> images;
        private List<String> tags;
        private long createdAt;
    }

    @Data
    public static class UserDto {
        private String userId;
        private String userName;
        private String avatar;
    }

    @Data
    public static class TripShortDto {
        private String tripId;
        private String title;
        private String coverPhoto;
    }

    @Data
    public static class LikesInfoDto {
        private int count;
        private boolean userLiked;
    }

    @Data
    public static class CommentDto {
        private String commentId;
        private String userId;
        private String userName;
        private String avatar;
        private String content;
        private long createdAt;
    }
}
