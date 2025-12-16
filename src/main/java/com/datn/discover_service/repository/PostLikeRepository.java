package com.datn.discover_service.repository;


import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.PostLike;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;


@Repository
public class PostLikeRepository {

    private final Firestore db = FirestoreClient.getFirestore();

    public boolean exists(String postId, String userId) throws Exception {
        QuerySnapshot snap = db.collection("post_likes")
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", userId)
                .get()
                .get();
        return !snap.isEmpty();
    }

    public void like(PostLike like) {
        db.collection("post_likes").add(like);
    }

    public void unlike(String postId, String userId) throws Exception {
        QuerySnapshot snap = db.collection("post_likes")
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", userId)
                .get()
                .get();

        for (DocumentSnapshot d : snap) {
            d.getReference().delete();
        }
    }

    public long countByPostId(String postId) throws Exception {
        return db.collection("post_likes")
                .whereEqualTo("postId", postId)
                .get()
                .get()
                .size();
    }
}




