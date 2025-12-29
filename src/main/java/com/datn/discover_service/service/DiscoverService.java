package com.datn.discover_service.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.ShareTripRequest;
import com.datn.discover_service.mapper.DiscoverMapper;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.TripRepository;
import com.datn.discover_service.repository.UsersRepository;
import com.google.cloud.Timestamp;

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
    // 🔥 Controller đang gọi
    // =========================
    public List<DiscoverItem> getDiscoverList(String viewerId, int page, int size) throws Exception {
        List<Trip> trips = tripRepository.getPublicTrips(page, size);
        return DiscoverMapper.toDiscoverItems(trips, viewerId, followRepository, usersRepository);
    }

    public List<DiscoverItem> getDiscoverListFollowing(String viewerId, int page, int size) throws Exception {
        List<String> followingIds = followRepository.getFollowingIds(viewerId);
        List<Trip> trips = tripRepository.getFollowerTrips(followingIds, page, size);
        return DiscoverMapper.toDiscoverItems(trips, viewerId, followRepository, usersRepository);
    }

    // =========================
    // 🔥 Share trip (Controller cần)
    // =========================
    public void shareTrip(ShareTripRequest request) throws Exception {
    Trip trip = tripRepository.getTrip(request.getTripId());
    if (trip == null) {
        throw new RuntimeException("Trip not found");
    }

    LocalDateTime now = LocalDateTime.now();
    Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();

    Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(
            instant.getEpochSecond(),
            instant.getNano()
    );

    trip.setSharedAt(timestamp);

    tripRepository.save(trip);
}
}
