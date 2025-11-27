package com.datn.discover_service.controller;

import com.datn.discover_service.dto.CommentDTO;
import com.datn.discover_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discover")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable String postId,
            @RequestHeader("userId") String userId,
            @RequestHeader("username") String username,
            @RequestHeader("avatar") String avatar,
            @RequestBody String content
    ) {
        CommentDTO comment = commentService.addComment(
                postId, userId, username, avatar, content
        );

        return ResponseEntity.ok(comment);
    }
    
    public static class CreateCommentRequest {
        public String content;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable String postId
    ) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }
}
