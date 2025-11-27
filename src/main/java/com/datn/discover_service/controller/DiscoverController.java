package com.datn.discover_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.dto.CreatePostRequest;
import com.datn.discover_service.service.DiscoverService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/discover")
@RequiredArgsConstructor
public class DiscoverController {

    private final DiscoverService discoverService;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestHeader("userId") String userId,
            @RequestHeader("username") String username,
            @RequestHeader("avatar") String avatar,
            @RequestBody CreatePostRequest request
    ) {
        String postId = discoverService.createPost(
                userId,
                username,
                avatar,
                request
        );

        return ResponseEntity.ok().body(
                new java.util.HashMap<String, Object>() {{
                    put("postId", postId);
                    put("message", "Post created successfully!");
                }}
        );
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(discoverService.getPosts(page, size));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable String postId) {
        return ResponseEntity.ok(discoverService.getPostDetail(postId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(discoverService.searchPosts(query));
    }


}
