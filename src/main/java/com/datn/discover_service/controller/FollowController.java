package com.datn.discover_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.model.User;
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
    @GetMapping("/{userId}/followers/raw")
    public List<User> getFollowersRaw(@PathVariable String userId) throws Exception {
        return followService.getFollowersRaw(userId);
    }

}
