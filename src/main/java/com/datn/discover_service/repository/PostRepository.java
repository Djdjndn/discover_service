package com.datn.discover_service.repository;

import com.datn.discover_service.model.Post;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepository {

    @Autowired
    private Firestore db;

    public List<Post> getPublicPosts(int page, int size, String sort) throws Exception {
        CollectionReference ref = db.collection("posts");

        Query query = ref.whereEqualTo("isPublic", true);

        if ("likes".equalsIgnoreCase(sort)) {
            query = query.orderBy("likesCount", Query.Direction.DESCENDING);
        } else {
            // default sort by createdAt desc
            query = query.orderBy("createdAt", Query.Direction.DESCENDING);
        }

        query = query.offset(page * size).limit(size);

        ApiFuture<QuerySnapshot> future = query.get();
        List<Post> list = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Post p = doc.toObject(Post.class);
            p.setPostId(doc.getId());
            list.add(p);
        }
        return list;
    }

    public Post getPost(String postId) throws Exception {
        DocumentSnapshot doc = db.collection("posts")
                .document(postId)
                .get()
                .get();
        if (!doc.exists()) return null;
        Post post = doc.toObject(Post.class);
        post.setPostId(doc.getId());
        return post;
    }

    public List<Post> getPostsByUserIds(List<String> userIds, int page, int size) throws Exception {
        // Firestore whereIn max 10 elements
        List<Post> result = new ArrayList<>();
        int from = 0;
        while (from < userIds.size()) {
            int to = Math.min(from + 10, userIds.size());
            List<String> sub = userIds.subList(from, to);

            ApiFuture<QuerySnapshot> future = db.collection("posts")
                    .whereEqualTo("isPublic", true)
                    .whereIn("userId", sub)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .offset(page * size)
                    .limit(size)
                    .get();

            for (DocumentSnapshot doc : future.get().getDocuments()) {
                Post p = doc.toObject(Post.class);
                p.setPostId(doc.getId());
                result.add(p);
            }
            from = to;
        }
        return result;
    }

    public List<Post> searchPublicPosts(String queryText, int page, int size) throws Exception {
        // Giả sử tags là array, search theo tags
        ApiFuture<QuerySnapshot> future = db.collection("posts")
                .whereEqualTo("isPublic", true)
                .whereArrayContains("tags", queryText)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .offset(page * size)
                .limit(size)
                .get();

        List<Post> list = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Post p = doc.toObject(Post.class);
            p.setPostId(doc.getId());
            list.add(p);
        }
        return list;
    }
}
