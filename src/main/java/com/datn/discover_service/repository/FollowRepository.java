package com.datn.discover_service.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.User;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

@Repository
public class FollowRepository {

    @Autowired
    private Firestore db;

    private static final String COLLECTION = "follows";
    private static final String FIELD_FOLLOWER_ID = "followerID";
    private static final String FIELD_FOLLOWING_ID = "followingID";

    // ========== CHECK FOLLOW ==========
    public boolean isFollowing(String followerId, String followingId) throws Exception {
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo(FIELD_FOLLOWER_ID, followerId)
                .whereEqualTo(FIELD_FOLLOWING_ID, followingId)
                .limit(1)
                .get()
                .get();

        return !snapshot.isEmpty();
    }

    // ========== FOLLOW ==========
    public void follow(String followerId, String followingId) throws Exception {

        if (isFollowing(followerId, followingId)) return;

        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_FOLLOWER_ID, followerId);
        data.put(FIELD_FOLLOWING_ID, followingId);
        data.put("createdAt", LocalDateTime.now().toString());

        db.collection(COLLECTION).add(data).get();
    }

    // ========== UNFOLLOW ==========
    public void unfollow(String followerId, String followingId) throws Exception {

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo(FIELD_FOLLOWER_ID, followerId)
                .whereEqualTo(FIELD_FOLLOWING_ID, followingId)
                .get()
                .get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            doc.getReference().delete().get();
        }
    }

    // ========== GET FOLLOWING IDS ==========
    public List<String> getFollowingIds(String followerId) throws Exception {

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo(FIELD_FOLLOWER_ID, followerId)
                .get()
                .get();

        List<String> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            String followingId = doc.getString(FIELD_FOLLOWING_ID);
            if (followingId != null) {
                result.add(followingId);
            }
        }
        return result;
    }

    public long countFollowers(String userId) throws Exception {
        return db.collection(COLLECTION)
            .whereEqualTo(FIELD_FOLLOWING_ID, userId)
            .get()
            .get()
            .size();
    }

    // ========== GET FOLLOWER IDS ==========
    // ========== GET FOLLOWERS (User list) ==========
    public List<User> getFollowers(String userId) throws Exception {

        // 1. Query bảng follows theo followingID
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo(FIELD_FOLLOWING_ID, userId)
                .get()
                .get();

        List<User> result = new ArrayList<>();

        // 2. Với mỗi followerID → lấy User
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            String followerId = doc.getString(FIELD_FOLLOWER_ID);
            if (followerId == null) continue;

            DocumentSnapshot userDoc = db
                    .collection("users")
                    .document(followerId)
                    .get()
                    .get();

            if (!userDoc.exists()) continue;

            User user = userDoc.toObject(User.class);

            // 🔒 đảm bảo id luôn có
            user.setId(userDoc.getId());

            result.add(user);
        }

        return result;
    }

    public List<String> findFollowerIdsByFollowingId(String userId) throws Exception {

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo(FIELD_FOLLOWING_ID, userId)
                .get()
                .get();

        List<String> result = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            String followerId = doc.getString(FIELD_FOLLOWER_ID);
            if (followerId != null) {
                result.add(followerId);
            }
        }
        return result;
    }
}
