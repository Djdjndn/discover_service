package com.datn.discover_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.ProfileResponse;
import com.datn.discover_service.mapper.DiscoverMapper;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.TripRepository;
import com.datn.discover_service.repository.UsersRepository;

@Service
public class ProfileService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<DiscoverItem> getUserTrips(
            String profileUserId,
            String viewerId,
            int page,
            int size
    ) throws Exception {

        boolean isOwner = profileUserId.equals(viewerId);
        boolean isFollower = false;

        if (!isOwner && viewerId != null) {
            isFollower = followRepository.isFollowing(viewerId, profileUserId);
        }

        List<Trip> trips = tripRepository.getTripsByUserForProfile(
                profileUserId,
                isOwner,
                isFollower,
                page,
                size
        );

        return DiscoverMapper.toDiscoverItems(
            trips,
            viewerId,
            followRepository,
            usersRepository
        );
    }

    public ProfileResponse getUserProfile(String userId) throws Exception {
        User user = usersRepository.getUser(userId);
        long followerCount = followRepository.countFollowers(userId);

        return new ProfileResponse(
            user.getId(),
            user.getFirstName() + " " + user.getLastName(),
            user.getProfilePicture(),
            followerCount
        );
}

}
