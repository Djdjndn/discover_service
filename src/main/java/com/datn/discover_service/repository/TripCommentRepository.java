package com.datn.discover_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.discover_service.model.TripComment;

public interface TripCommentRepository extends JpaRepository<TripComment, Long> {
    List<TripComment> findByPostIdOrderByCreatedAtDesc(String postId);
}
