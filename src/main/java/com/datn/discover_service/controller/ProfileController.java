package com.datn.discover_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.FollowerResponse;
import com.datn.discover_service.dto.ProfileResponse;
import com.datn.discover_service.service.FollowService;
import com.datn.discover_service.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FollowService followService;

    @GetMapping("/{userId}/trips")
    public List<DiscoverItem> getUserTrips(
            @PathVariable String userId,
            @RequestParam(required = false) String viewerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        return profileService.getUserTrips(userId, viewerId, page, size);
    }

    @GetMapping("/{userId}")
        public ProfileResponse getUserProfile(@PathVariable String userId) throws Exception {
        return profileService.getUserProfile(userId);
    }

    @GetMapping("/{userId}/followers")
    public List<FollowerResponse> getFollowers(@PathVariable String userId) throws Exception {
        return followService.getFollowers(userId);
    }
}