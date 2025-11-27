package com.datn.discover_service.controller;

import com.datn.discover_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discover")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable String postId,
            @RequestHeader("userId") String userId
    ) {
        likeService.likePost(postId, userId);
        return ResponseEntity.ok("Liked successfully!");
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<?> unlikePost(
            @PathVariable String postId,
            @RequestHeader("userId") String userId
    ) {
        likeService.unlikePost(postId, userId);
        return ResponseEntity.ok("Unliked successfully!");
    }
}
