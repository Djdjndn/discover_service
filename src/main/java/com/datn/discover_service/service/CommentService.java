package com.datn.discover_service.service;

import com.datn.discover_service.dto.CommentDTO;
import com.datn.discover_service.model.TripComment;
import com.datn.discover_service.model.TripPost;
import com.datn.discover_service.repository.TripCommentRepository;
import com.datn.discover_service.repository.TripPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TripCommentRepository tripCommentRepository;
    private final TripPostRepository tripPostRepository;

    // CREATE COMMENT
    public CommentDTO addComment(String postId, String userId, String username,
                                 String avatar, String content) {

        TripPost post = tripPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        TripComment comment = TripComment.builder()
                .postId(postId)
                .userId(userId)
                .username(username)
                .avatar(avatar)
                .content(content)
                .createdAt(System.currentTimeMillis())
                .build();

        comment = tripCommentRepository.save(comment);

        // tăng comments_count
        post.setCommentsCount(post.getCommentsCount() + 1);
        tripPostRepository.save(post);

        return CommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .username(comment.getUsername())
                .avatar(comment.getAvatar())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    // GET COMMENT LIST
    public List<CommentDTO> getComments(String postId) {

        List<TripComment> comments =
                tripCommentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        return comments.stream().map(c ->
                CommentDTO.builder()
                        .id(c.getId())
                        .postId(c.getPostId())
                        .userId(c.getUserId())
                        .username(c.getUsername())
                        .avatar(c.getAvatar())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build()
        ).toList();
    }
}
