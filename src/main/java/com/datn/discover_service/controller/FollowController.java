package com.datn.discover_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.service.FollowService;



@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping
    public void follow(
            @RequestParam String followerId,
            @RequestParam String followingId
    ) throws Exception {
        followService.follow(followerId, followingId);
    }

    @DeleteMapping
    public void unfollow(
            @RequestParam String followerId,
            @RequestParam String followingId
    ) throws Exception {
        followService.unfollow(followerId, followingId);
    }

    @GetMapping("/status")
    public boolean isFollowing(
            @RequestParam String followerId,
            @RequestParam String followingId
    ) throws Exception {
        return followService.isFollowing(followerId, followingId);
    }
}
