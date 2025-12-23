package com.datn.discover_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.service.DiscoverService;

@RestController
@RequestMapping("/api/discover")
public class DiscoverController {

    @Autowired
    private DiscoverService discoverService;

    // Explore / Random
    // FE đang gọi: /api/discover?page=0&size=10
    // ✅ thêm userId để trả isFollowing đúng
    @GetMapping
    public List<DiscoverItem> getDiscover(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return discoverService.getDiscoverList(userId, page, size);
    }

    // Following feed
    @GetMapping("/following")
    public List<DiscoverItem> getDiscoverFollowing(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return discoverService.getDiscoverListFollowing(userId, page, size);
    }
}
