package com.datn.discover_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.discover_service.repository.FollowRepository;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    public void follow(String followerId, String followingId) throws Exception {
        followRepository.follow(followerId, followingId);
    }

    public void unfollow(String followerId, String followingId) throws Exception {
        followRepository.unfollow(followerId, followingId);
    }

    public boolean isFollowing(String followerId, String followingId) throws Exception {
        return followRepository.isFollowing(followerId, followingId);
    }
}
