package com.datn.discover_service.repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.dto.SharedUser;
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
                .whereEqualTo("isPublic", "public") // GIỮ NGUYÊN
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

        // ✅ CHỈ SỬA Ở ĐÂY: cho phép member đi lên service để filter
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
        String tags,
        String isPublic,
        List<SharedUser> sharedWithUsers
    ) throws Exception {
        db.collection(COLLECTION)
            .document(tripId)
            .update(
                "sharedAt", Timestamp.now(),
                "isPublic", isPublic,
                "content", content,
                "tags", tags,
                "sharedWithUsers", sharedWithUsers
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

        // startDate / endDate
        String startDate = doc.getString("startDate");
        if (startDate != null) {
            trip.setStartDate(LocalDate.parse(startDate));
        }

        String endDate = doc.getString("endDate");
        if (endDate != null) {
            trip.setEndDate(LocalDate.parse(endDate));
        }

        // sharedAt
        Object sharedAtObj = doc.get("sharedAt");
        if (sharedAtObj instanceof Timestamp ts) {
            trip.setSharedAt(
                ts.toDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            );
        }

        // sharedWithUsers (🔥 MAP TAY – KHÔNG toObject)
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawUsers =
            (List<Map<String, Object>>) doc.get("sharedWithUsers");

        List<SharedUser> sharedWith = new ArrayList<>();
        if (rawUsers != null) {
            for (Map<String, Object> m : rawUsers) {
                SharedUser su = new SharedUser();
                su.setId((String) m.get("id"));
                su.setFirstName((String) m.get("firstName"));
                su.setLastName((String) m.get("lastName"));
                su.setEmail((String) m.get("email"));
                su.setProfilePicture((String) m.get("profilePicture"));
                su.setRole((String) m.get("role"));
                su.setEnabled((Boolean) m.get("enabled"));
                sharedWith.add(su);
            }
        }

        trip.setSharedWithUsers(sharedWith);

        return trip;
    }


    private boolean contains(String source, String kw) {
        return source != null && source.toLowerCase().contains(kw);
    }
}
