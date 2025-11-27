package com.datn.discover_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.datn.discover_service.model.TripPost;

public interface TripPostRepository extends JpaRepository<TripPost, String> {
    @Query("SELECT t FROM TripPost t " +
       "WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR LOWER(t.ownerName) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR LOWER(t.shortDescription) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TripPost> searchSimple(@Param("query") String query);

}
