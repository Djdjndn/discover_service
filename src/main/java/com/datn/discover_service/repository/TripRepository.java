package com.datn.discover_service.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.Trip;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

@Repository
public class TripRepository {

    @Autowired
    private Firestore db;

    private static final String COLLECTION = "trips";

    // =========================
    // Get single trip (helper)
    // =========================
    public Trip getTrip(String tripId) throws Exception {
        DocumentSnapshot doc = db.collection(COLLECTION)
                .document(tripId)
                .get()
                .get();

        if (!doc.exists()) return null;
        return mapDocToTrip(doc);
    }

    // ✅ ADD: Optional findById (để PlanService dùng .findById)
    public Optional<Trip> findById(String tripId) throws Exception {
        return Optional.ofNullable(getTrip(tripId));
    }

    // ✅ ADD: save() chỉ update field cần thiết (likeCount)
    public void save(Trip trip) throws Exception {
        if (trip == null || trip.getId() == null) {
            throw new IllegalArgumentException("Trip ID must not be null");
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("likeCount", trip.getLikeCount());

        db.collection(COLLECTION)
                .document(trip.getId())
                .set(updates, SetOptions.merge())
                .get();
    }

    // =========================
    // Discover - Random / Explore
    // isPublic = "public"
    // order by sharedAt desc
    // =========================
    public List<Trip> getPublicTrips(int page, int size) throws Exception {

        Query query = db.collection(COLLECTION)
                .whereEqualTo("isPublic", "public")
                .orderBy("sharedAt", Query.Direction.DESCENDING)
                .limit(size);

        QuerySnapshot snapshot = query.get().get();

        List<Trip> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(mapDocToTrip(doc));
        }

        return result;
    }

    // =========================
    // Discover - Following
    // isPublic = "follower"
    // userId in followingIds
    // =========================
    public List<Trip> getFollowerTrips(
            List<String> followingIds,
            int page,
            int size
    ) throws Exception {

        if (followingIds == null || followingIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = db.collection(COLLECTION)
                .whereIn("userId", followingIds)
                .whereIn("isPublic", List.of("public", "follower"))
                .orderBy("sharedAt", Query.Direction.DESCENDING)
                .limit(size);

        QuerySnapshot snapshot = query.get().get();

        List<Trip> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(mapDocToTrip(doc));
        }

        return result;
    }

    // =========================
    // Profile trips
    // =========================
    public List<Trip> getTripsByUserForProfile(
            String userId,
            boolean isOwner,
            boolean isFollower,
            int page,
            int size
    ) throws Exception {

        Query query = db.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("sharedAt", Query.Direction.DESCENDING)
                .limit(size);

        if (!isOwner) {
            if (isFollower) {
                query = query.whereIn("isPublic", List.of("public", "follower"));
            } else {
                query = query.whereEqualTo("isPublic", "public");
            }
        }

        QuerySnapshot snapshot = query.get().get();

        List<Trip> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(mapDocToTrip(doc));
        }

        return result;
    }

    // =========================
    // Mapper
    // =========================
    private Trip mapDocToTrip(DocumentSnapshot doc) {

        Trip trip = new Trip();

        trip.setId(doc.getId());
        trip.setUserId(doc.getString("userId"));
        trip.setTitle(doc.getString("title"));
        trip.setCoverPhoto(doc.getString("coverPhoto"));
        trip.setContent(doc.getString("content"));
        trip.setTags(doc.getString("tags"));
        trip.setIsPublic(doc.getString("isPublic"));

        // ✅ FIX: sharedAt là Timestamp (đúng theo Firestore)
        Object sharedAtObj = doc.get("sharedAt");
        if (sharedAtObj instanceof Timestamp ts) {
            trip.setSharedAt(ts);
        } else {
            trip.setSharedAt(null);
        }

        // likeCount (nếu có)
        Long likeCount = doc.getLong("likeCount");
        if (likeCount != null) {
            trip.setLikeCount(likeCount.intValue());
        }

        return trip;
    }

    public List<Trip> searchPublicTrips(String keyword) {
        try {
            String kw = keyword.toLowerCase();

            QuerySnapshot snapshot = db.collection(COLLECTION)
                    .whereEqualTo("isPublic", "public")
                    .get()
                    .get();

            List<Trip> result = new ArrayList<>();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Trip trip = mapDocToTrip(doc);

                if (
                    contains(trip.getTitle(), kw)
                    || contains(trip.getTags(), kw)
                    || contains(trip.getContent(), kw)
                ) {
                    result.add(trip);
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }



    private boolean contains(String source, String kw) {
        return source != null && source.toLowerCase().contains(kw);
    }
}


