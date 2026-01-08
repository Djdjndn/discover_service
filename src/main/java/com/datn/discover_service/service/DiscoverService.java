package com.datn.discover_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.ShareTripRequest;
import com.datn.discover_service.dto.SharedUser;
import com.datn.discover_service.mapper.DiscoverMapper;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.TripRepository;
import com.datn.discover_service.repository.UsersRepository;

@Service
public class DiscoverService {

    private final TripRepository tripRepository;
    private final FollowRepository followRepository;
    private final UsersRepository usersRepository;

    public DiscoverService(
            TripRepository tripRepository,
            FollowRepository followRepository,
            UsersRepository usersRepository
    ) {
        this.tripRepository = tripRepository;
        this.followRepository = followRepository;
        this.usersRepository = usersRepository;
    }

    // =========================
    //  Discover - Public
    // =========================
    public List<DiscoverItem> getDiscoverList(String viewerId, int page, int size) throws Exception {
        // Public feed: CHỈ public
        List<Trip> trips = tripRepository.getPublicTrips(page, size);
        return DiscoverMapper.toDiscoverItems(trips, viewerId, followRepository, usersRepository);
    }

    // =========================
    //  Discover - Following (CÓ MEMBER)
    // =========================
    public List<DiscoverItem> getDiscoverListFollowing(String viewerId, int page, int size) throws Exception {

        // 1. Lấy danh sách đang follow
        List<String> followingIds = followRepository.getFollowingIds(viewerId);

        // 2. 🔥 BẮT BUỘC thêm chính mình
        List<String> userIds = new ArrayList<>(followingIds);
        userIds.add(viewerId);

        // 3. Query Firestore (có cả trip của mình)
        List<Trip> trips = tripRepository.getFollowerTrips(userIds, page, size);

        // 4. Filter quyền xem (member / follower / public)
        List<Trip> visibleTrips = trips.stream()
            .filter(trip -> canViewTrip(trip, viewerId, followingIds))
            .toList();

        return DiscoverMapper.toDiscoverItems(
            visibleTrips,
            viewerId,
            followRepository,
            usersRepository
        );
    }



    // =========================
    //  Share trip
    // =========================
    public void shareTrip(ShareTripRequest request) throws Exception {

        Trip trip = tripRepository.getTrip(request.getTripId());
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        String isPublic = request.getIsPublic() != null
                ? request.getIsPublic()
                : trip.getIsPublic();

        String content = request.getContent() != null
                ? request.getContent()
                : trip.getContent();

        String tags = request.getTags() != null
                ? request.getTags()
                : trip.getTags();

        List<SharedUser> sharedUsers;

        if (request.getSharedWithUsers() != null) {

            if (request.getSharedWithUsers().isEmpty()) {
                sharedUsers = List.of();

            } else {
                List<User> users = usersRepository.findUsersByIds(
                        request.getSharedWithUsers()
                );

                sharedUsers = users.stream()
                        .map(u -> {
                            SharedUser su = new SharedUser();
                            su.setId(u.getId());
                            su.setFirstName(u.getFirstName());
                            su.setLastName(u.getLastName());
                            su.setEmail(u.getEmail());
                            su.setProfilePicture(u.getProfilePicture());
                            su.setRole(u.getRole());
                            su.setEnabled(u.getEnabled());
                            return su;
                        })
                        .toList();
            }

        } else {
            sharedUsers = trip.getSharedWithUsers();
        }

        // Update Firestore
        tripRepository.updateShareInfo(
                trip.getId(),
                content,
                tags,
                isPublic,
                sharedUsers
        );
    }




    private boolean canViewTrip(
        Trip trip,
        String viewerId,
        List<String> followingIds
    ) {
        // Chủ trip luôn thấy
        if (viewerId.equals(trip.getUserId())) {
            return true;
        }
        
        // if (trip.getMemberIds() != null
        //     && trip.getMemberIds().contains(viewerId)) {
        //      return true;
        // }
        
        String isPublic = trip.getIsPublic();
        if (isPublic == null) return false;

        // Public
        if ("public".equals(isPublic)) {
            return true;
        }

        // Follower
        if ("follower".equals(isPublic)) {

            if (!followingIds.contains(trip.getUserId())) {
                return false;
            }

            List<SharedUser> shared = trip.getSharedWithUsers();

            // Follower thường
            if (shared == null || shared.isEmpty()) {
                return true;
            }

            // Follower custom
            return shared.stream()
                    .anyMatch(u -> viewerId.equals(u.getId()));
        }

        return false;
    }


}
