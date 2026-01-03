package com.datn.discover_service.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.Trip;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

@Repository
public class TripRepository {

    @Autowired
    private Firestore db;

    private static final String COLLECTION = "trips";

    // =========================
    // Get single trip
    // =========================
    public Trip getTrip(String tripId) throws Exception {
        DocumentSnapshot doc = db.collection(COLLECTION)
                .document(tripId)
                .get()
                .get();

        if (!doc.exists()) return null;
        return mapDocToTrip(doc);
    }

    public Optional<Trip> findById(String tripId) throws Exception {
        return Optional.ofNullable(getTrip(tripId));
    }

    /**
     * discover_service KHÔNG được overwrite Trip
     */
    public void save(Trip trip) {
        // NO-OP (đúng kiến trúc)
    }

    // =========================
    // Discover - Public
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
    // Profile
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
    // Search
    // =========================
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

    // =========================
    // Update share info
    // =========================
    public void updateShareInfo(
            String tripId,
            String content,
            String tags
    ) throws Exception {

        db.collection(COLLECTION)
                .document(tripId)
                .update(
                        "sharedAt", Timestamp.now(),
                        "isPublic", "public",
                        "content", content,
                        "tags", tags
                )
                .get();
    }

    public void updateLikeCount(String tripId, int delta) throws Exception {

        db.collection(COLLECTION)
                .document(tripId)
                .update("likeCount", FieldValue.increment(delta))
                .get();
    }

    // =========================
    // Mapper (QUAN TRỌNG NHẤT)
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

        // startDate / endDate (String -> LocalDate)
        String startDate = doc.getString("startDate");
        if (startDate != null) {
            trip.setStartDate(LocalDate.parse(startDate));
        }

        String endDate = doc.getString("endDate");
        if (endDate != null) {
            trip.setEndDate(LocalDate.parse(endDate));
        }

        // ✅ FIX CUỐI CÙNG: sharedAt (String | Timestamp | null)
        Object sharedAtObj = doc.get("sharedAt");

        if (sharedAtObj instanceof Timestamp ts) {
            trip.setSharedAt(
                ts.toDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            );
        } 
        else if (sharedAtObj instanceof String s) {
            trip.setSharedAt(LocalDateTime.parse(s));
        }
        else {
            trip.setSharedAt(null);
        }

        return trip;
    }


    private boolean contains(String source, String kw) {
        return source != null && source.toLowerCase().contains(kw);
    }
}
