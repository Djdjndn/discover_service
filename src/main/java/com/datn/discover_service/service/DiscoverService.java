package com.datn.discover_service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.TripRepository;
import com.datn.discover_service.repository.UsersRepository;

@Service
public class DiscoverService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FollowRepository followRepository;

    // ==================================================
    // API 1: DISCOVER - EXPLORE (PUBLIC)
    // ==================================================
    public List<DiscoverItem> getDiscoverList(
            String currentUserId,
            int page,
            int size
    ) throws Exception {

        List<Trip> trips = tripRepository.getPublicTrips(page, size);

        List<String> followingIds = currentUserId == null
                ? Collections.emptyList()
                : followRepository.getFollowingIds(currentUserId);

        return mapTripsToDiscoverItems(trips, currentUserId, followingIds);
    }

    // ==================================================
    // API 2: DISCOVER - FOLLOWING
    // ==================================================
    public List<DiscoverItem> getDiscoverListFollowing(
            String currentUserId,
            int page,
            int size
    ) throws Exception {

        if (currentUserId == null) {
            return new ArrayList<>();
        }

        List<String> followingIds = followRepository.getFollowingIds(currentUserId);
        if (followingIds == null || followingIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Trip> trips = tripRepository.getFollowerTrips(followingIds, page, size);
        return mapTripsToDiscoverItems(trips, currentUserId, followingIds);
    }

    // ==================================================
    // HELPER: MAP Trip -> DiscoverItem (NO FIRESTORE QUERY)
    // ==================================================
    private List<DiscoverItem> mapTripsToDiscoverItems(
            List<Trip> trips,
            String currentUserId,
            List<String> followingIds
    ) throws Exception {

        List<DiscoverItem> result = new ArrayList<>();
        if (trips == null || trips.isEmpty()) return result;

        for (Trip trip : trips) {
            if (trip == null) continue;

            User user = usersRepository.getUser(trip.getUserId());

            boolean isFollowing = false;
            if (currentUserId != null
                    && trip.getUserId() != null
                    && !currentUserId.equals(trip.getUserId())
                    && followingIds != null) {
                isFollowing = followingIds.contains(trip.getUserId());
            }

            DiscoverItem item = new DiscoverItem();
            item.setTripId(trip.getId());
            item.setCaption(trip.getContent());
            item.setTripImage(trip.getCoverPhoto());
            item.setTags(trip.getTags());
            item.setIsPublic(trip.getIsPublic());
            item.setSharedAt(trip.getSharedAt());
            item.setFollowing(isFollowing);

            if (user != null) {
                item.setUserId(user.getId());
                item.setUserName(
                        (user.getFirstName() == null ? "" : user.getFirstName()) + " " +
                        (user.getLastName() == null ? "" : user.getLastName())
                );
                item.setUserAvatar(user.getProfilePicture());
            }

            result.add(item);
        }

        return result;
    }
}
