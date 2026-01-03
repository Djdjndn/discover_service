package com.datn.discover_service.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.UsersRepository;

public class DiscoverMapper {

    public static List<DiscoverItem> toDiscoverItems(
            List<Trip> trips,
            String viewerId,
            FollowRepository followRepository,
            UsersRepository usersRepository
    ) throws Exception {

        List<DiscoverItem> result = new ArrayList<>();
        if (trips == null || trips.isEmpty()) return result;

        for (Trip trip : trips) {
            if (trip == null) continue;

            User user = usersRepository.getUser(trip.getUserId());

            boolean isFollowing = false;
            if (viewerId != null
                    && trip.getUserId() != null
                    && !trip.getUserId().equals(viewerId)) {
                isFollowing = followRepository.isFollowing(viewerId, trip.getUserId());
            }

            DiscoverItem item = new DiscoverItem();
            item.setTripId(trip.getId());
            item.setCaption(trip.getContent());
            item.setTripImage(trip.getCoverPhoto());
            item.setTags(trip.getTags());
            item.setIsPublic(trip.getIsPublic());
            item.setFollowing(isFollowing);

            // ✅ FIX CHUẨN: Timestamp → LocalDateTime
            LocalDateTime sharedAt = trip.getSharedAt();
            if (sharedAt != null) {
                item.setSharedAt(sharedAt);
            }

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
