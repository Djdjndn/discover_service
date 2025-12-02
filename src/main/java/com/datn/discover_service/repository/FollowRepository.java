package com.datn.discover_service.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

@Repository
public class FollowRepository {

    @Autowired
    private Firestore db;

    public List<String> getFollowingIds(String userId) throws Exception {
        ApiFuture<QuerySnapshot> future = db.collection("follows")
                .whereEqualTo("followerId", userId)
                .get();

        List<String> result = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            String followingId = doc.getString("followingId");
            if (followingId != null) {
                result.add(followingId);
            }
        }
        return result;
    }
}
