package com.datn.discover_service.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.Trip;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

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

        // NOTE: page > 0 thì dùng startAfter (để sau nếu cần)
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

        // =========================
        // sharedAt (String OR Timestamp)
        // =========================
        Object sharedAtObj = doc.get("sharedAt");

        if (sharedAtObj instanceof com.google.cloud.Timestamp ts) {
            trip.setSharedAt(
                ts.toDate()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime()
            );
        } else if (sharedAtObj instanceof String str) {
            try {
                trip.setSharedAt(java.time.LocalDateTime.parse(str));
            } catch (Exception e) {
                trip.setSharedAt(null);
            }
        } else {
            trip.setSharedAt(null);
        }

        return trip;
    }
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
                // public + follower
                query = query.whereIn("isPublic", List.of("public", "follower"));
            } else {
                // chỉ public
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

}
