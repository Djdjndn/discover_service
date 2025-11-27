package com.datn.discover_service.service;

import com.datn.discover_service.model.TripLike;
import com.datn.discover_service.model.TripPost;
import com.datn.discover_service.repository.TripLikeRepository;
import com.datn.discover_service.repository.TripPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final TripLikeRepository tripLikeRepository;
    private final TripPostRepository tripPostRepository;

    // LIKE
    public void likePost(String postId, String userId) {

        // 1. kiểm tra đã like chưa
        if (tripLikeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new RuntimeException("User already liked this post");
        }

        // 2. tạo like mới
        TripLike like = TripLike.builder()
                .postId(postId)
                .userId(userId)
                .createdAt(System.currentTimeMillis())
                .build();

        tripLikeRepository.save(like);

        // 3. tăng likes_count
        TripPost post = tripPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setLikesCount(post.getLikesCount() + 1);
        tripPostRepository.save(post);
    }

    // UNLIKE
    public void unlikePost(String postId, String userId) {

        var like = tripLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("User has not liked this post"));

        tripLikeRepository.delete(like);

        // 2. giảm likes_count
        TripPost post = tripPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        tripPostRepository.save(post);
    }
}
