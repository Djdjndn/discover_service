package com.datn.discover_service.repository;

import com.datn.discover_service.model.Comment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {

    @Autowired
    private Firestore db;

    public List<Comment> getComments(String postId) throws Exception {
        ApiFuture<QuerySnapshot> future = db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get();

        List<Comment> list = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Comment c = doc.toObject(Comment.class);
            c.setCommentId(doc.getId());
            list.add(c);
        }
        return list;
    }

    public int getCommentCount(String postId) throws Exception {
        return getComments(postId).size();
    }
}
