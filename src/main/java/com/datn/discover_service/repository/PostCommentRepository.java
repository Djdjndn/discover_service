package com.datn.discover_service.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.PostComment;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;


@Repository
public class PostCommentRepository {

    private final Firestore db = FirestoreClient.getFirestore();

    public void save(PostComment c) {
        db.collection("post_comments").add(c);
    }

    public List<PostComment> findByPostId(String postId) throws Exception {
        QuerySnapshot snap = db.collection("post_comments")
                .whereEqualTo("postId", postId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get();

        List<PostComment> list = new ArrayList<>();
        for (DocumentSnapshot d : snap) {
            PostComment c = d.toObject(PostComment.class);
            c.setId(d.getId());
            list.add(c);
        }
        return list;
    }

    public long countByPostId(String postId) throws Exception {
        return db.collection("post_comments")
                .whereEqualTo("postId", postId)
                .get()
                .get()
                .size();
    }
}

