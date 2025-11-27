package com.datn.discover_service.service;

import com.datn.discover_service.dto.CreatePostRequest;
import com.datn.discover_service.dto.TripPostDetailDTO;
import com.datn.discover_service.dto.TripPostListDTO;
import com.datn.discover_service.model.TripPost;
import com.datn.discover_service.repository.TripPostRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
@RequiredArgsConstructor
public class DiscoverService {

    private final TripPostRepository tripPostRepository;
    private final TripServiceClient tripServiceClient;

    public String createPost(String userId,
                             String username,
                             String avatar,
                             CreatePostRequest request) {

        // 1) Gọi sang Trip Service để lấy metadata của trip thật
        Map<String, Object> tripData = tripServiceClient.getTripDetail(request.getTripId());

        if (tripData == null) {
            throw new RuntimeException("Trip không tồn tại hoặc Trip Service không phản hồi");
        }

        // 2) Extract metadata từ trip thật
        String title = (String) tripData.get("title");
        List<String> locations = (List<String>) tripData.get("locations");

        // 3) Tạo postId
        String postId = UUID.randomUUID().toString();

        // 4) Tạo entity TripPost
        TripPost post = TripPost.builder()
                .postId(postId)
                .tripId(request.getTripId())
                .ownerId(userId)
                .ownerName(username)
                .ownerAvatar(avatar)
                .title(title)
                .shortDescription(request.getShortDescription())
                .thumbnail(request.getThumbnail())
                .images(ListToJson(request.getImages()))   // convert list -> JSON string
                .locations(ListToJson(locations))
                .likesCount(0)
                .commentsCount(0)
                .createdAt(System.currentTimeMillis())
                .build();

        // 5) Lưu vào DB
        tripPostRepository.save(post);

        return postId;
    }

    // Helper: convert list to JSON string
    private String ListToJson(List<String> list) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    public List<TripPostListDTO> getPosts(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TripPost> posts = tripPostRepository.findAll(pageRequest);

        return posts.stream().map(post ->
                TripPostListDTO.builder()
                        .postId(post.getPostId())
                        .tripId(post.getTripId())
                        .ownerName(post.getOwnerName())
                        .ownerAvatar(post.getOwnerAvatar())
                        .title(post.getTitle())
                        .thumbnail(post.getThumbnail())
                        .locations(jsonToList(post.getLocations()))
                        .likesCount(post.getLikesCount())
                        .commentsCount(post.getCommentsCount())
                        .createdAt(post.getCreatedAt())
                        .build()
        ).toList();
    }

    private List<String> jsonToList(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(json, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    public TripPostDetailDTO getPostDetail(String postId) {

        TripPost post = tripPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return TripPostDetailDTO.builder()
                .postId(post.getPostId())
                .tripId(post.getTripId())
                .ownerId(post.getOwnerId())
                .ownerName(post.getOwnerName())
                .ownerAvatar(post.getOwnerAvatar())
                .title(post.getTitle())
                .shortDescription(post.getShortDescription())
                .thumbnail(post.getThumbnail())
                .images(jsonToList(post.getImages()))
                .locations(jsonToList(post.getLocations()))
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
    public List<TripPostListDTO> searchPosts(String query) {

        List<TripPost> posts = tripPostRepository.searchSimple(query);

        return posts.stream().map(post ->
                TripPostListDTO.builder()
                        .postId(post.getPostId())
                        .tripId(post.getTripId())
                        .ownerName(post.getOwnerName())
                        .ownerAvatar(post.getOwnerAvatar())
                        .title(post.getTitle())
                        .thumbnail(post.getThumbnail())
                        .locations(jsonToList(post.getLocations()))
                        .likesCount(post.getLikesCount())
                        .commentsCount(post.getCommentsCount())
                        .createdAt(post.getCreatedAt())
                        .build()
        ).toList();
    }



}
