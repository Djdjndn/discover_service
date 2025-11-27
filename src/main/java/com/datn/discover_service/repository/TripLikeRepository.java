package com.datn.discover_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.discover_service.model.TripLike;

public interface TripLikeRepository extends JpaRepository<TripLike, Long> {

    Optional<TripLike> findByPostIdAndUserId(String postId, String userId);

    int countByPostId(String postId);
}
