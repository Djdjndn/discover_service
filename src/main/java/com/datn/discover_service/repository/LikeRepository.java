package com.datn.discover_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.Firestore;

@Repository
public class LikeRepository {

    @Autowired
    private Firestore db;

    public int getLikeCount(String postId) throws Exception {
        return db.collection("posts")
                .document(postId)
                .collection("likes")
                .get()
                .get()
                .size();
    }

    public boolean isUserLiked(String postId, String userId) throws Exception {
        var doc = db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .get()
                .get();
        return doc.exists();
    }
}
