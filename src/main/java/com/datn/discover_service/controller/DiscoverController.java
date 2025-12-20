package com.datn.discover_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.dto.CreatePostCommentRequest;
import com.datn.discover_service.dto.CreatePostRequest;
import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.PostResponse;
import com.datn.discover_service.model.PostComment;
import com.datn.discover_service.service.DiscoverService;

@RestController
@RequestMapping("/api/discover")
public class DiscoverController {

    @Autowired
    private DiscoverService discoverService;

    // =========================
    // API 1: Discover chung
    // =========================
    @GetMapping
    public ResponseEntity<List<DiscoverItem>> getDiscover(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort
    ) throws Exception {
        return ResponseEntity.ok(
                discoverService.getDiscoverList(page, size, sort)
        );
    }

    // =========================
    // API 2: Discover following
    // =========================
    @GetMapping("/following")
    public ResponseEntity<List<DiscoverItem>> getDiscoverFollowing(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return ResponseEntity.ok(
                discoverService.getDiscoverListFollowing(userId, page, size)
        );
    }

    // =========================
    // API 3: Get Post (FEED / CREATE / COMMENT)
    // =========================
    @GetMapping("/posts/{postId}")
        public ResponseEntity<PostResponse> getPost(
                @PathVariable String postId,
                @RequestParam(required = false) String userId
        ) throws Exception {
        return ResponseEntity.ok(
                discoverService.buildPostResponse(postId, userId)
        );
}
    // =========================
    // API 4: Create Post
    // =========================
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody CreatePostRequest request
    ) throws Exception {

        String postId = discoverService.createPost(request);

        // trả luôn PostResponse cho FE add vào feed
        return ResponseEntity.ok(
                discoverService.buildPostResponse(postId, request.getUserId())
        );
    }

    // =========================
    // API 5: Like / Unlike
    // =========================
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable String postId,
            @RequestParam String userId
    ) throws Exception {
        discoverService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable String postId,
            @RequestParam String userId
    ) throws Exception {
        discoverService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    // =========================
    // API 6: Comment
    // =========================
    @PostMapping("/posts/{postId}/comments")
        public ResponseEntity<Void> addComment(
                @PathVariable String postId,
                @RequestBody CreatePostCommentRequest request
        ) throws Exception {
                discoverService.addPostComment(postId, request);
                return ResponseEntity.ok().build();
        }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<PostComment>> getComments(
            @PathVariable String postId
    ) throws Exception {
        return ResponseEntity.ok(
                discoverService.getPostComments(postId)
        );
    }
}
