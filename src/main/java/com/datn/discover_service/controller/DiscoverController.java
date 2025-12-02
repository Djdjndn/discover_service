package com.datn.discover_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.PostDetailResponse;
import com.datn.discover_service.service.DiscoverService;

@RestController
@RequestMapping("/api/discover")
public class DiscoverController {

    @Autowired
    private DiscoverService discoverService;

    // API 1: Danh sách Discover chung
    @GetMapping
    public ResponseEntity<List<DiscoverItem>> getDiscover(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) throws Exception {
        return ResponseEntity.ok(discoverService.getDiscoverList(page, size, sort));
    }

    // API 2: Danh sách Discover chỉ bài của người mình follow
    @GetMapping("/following")
    public ResponseEntity<List<DiscoverItem>> getDiscoverFollowing(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return ResponseEntity.ok(discoverService.getDiscoverListFollowing(userId, page, size));
    }

    // API 3: Chi tiết bài viết
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable String postId,
            @RequestParam(required = false) String userId
    ) throws Exception {
        PostDetailResponse resp = discoverService.getPostDetail(postId, userId);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    // API 4: Search bài viết
    @GetMapping("/search")
    public ResponseEntity<List<DiscoverItem>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return ResponseEntity.ok(discoverService.search(query, page, size));
    }

    // API 5: Filter theo địa điểm
    @GetMapping("/filter")
    public ResponseEntity<List<DiscoverItem>> filterByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "50") double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return ResponseEntity.ok(discoverService.filterByLocation(lat, lng, radiusKm, page, size));
    }
}
