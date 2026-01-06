package com.datn.discover_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.FollowerResponse;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.UsersRepository;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UsersRepository usersRepository;

    public void follow(String followerId, String followingId) throws Exception {
        followRepository.follow(followerId, followingId);
    }

    public void unfollow(String followerId, String followingId) throws Exception {
        followRepository.unfollow(followerId, followingId);
    }

    public boolean isFollowing(String followerId, String followingId) throws Exception {
        return followRepository.isFollowing(followerId, followingId);
    }

    public List<FollowerResponse> getFollowers(String userId) throws Exception {
    List<User> users = followRepository.getFollowers(userId);

        return users.stream().map(u ->
            new FollowerResponse(
                u.getId(),
                u.getFirstName() + " " + u.getLastName(),
                u.getProfilePicture()
            )
        ).toList();
    }

    public List<User> getFollowersRaw(String userId) throws Exception {

        List<String> followerIds = followRepository.findFollowerIdsByFollowingId(userId);

        if (followerIds.isEmpty()) {
            return List.of();
        }

        return usersRepository.findUsersByIds(followerIds);
    }


}