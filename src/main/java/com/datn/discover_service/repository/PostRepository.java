package com.datn.discover_service.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.Post;
import com.datn.discover_service.model.PostData;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

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

        // Lưu post dùng khi tạo bài viết mới
    public void createPost(Post post) throws Exception {
        db.collection("posts")
            .document(post.getPostId())
            .set(post)
            .get();
    }

    // Nếu bạn có nhu cầu lưu PostData (optional)
    public void savePostData(PostData post) throws Exception {
        db.collection("posts")
            .document(post.getPostId())
            .set(post)
            .get();
    }

    public List<Post> searchPublicPostsFlexible(String queryText, int page, int size) throws Exception {
    String q = queryText == null ? "" : queryText.trim().toLowerCase();
    if (q.isEmpty()) return new ArrayList<>();

    // Lấy nhiều hơn để filter “contains” ở server-side (Firestore không hỗ trợ LIKE contains)
    int fetchLimit = Math.min(250, Math.max(50, (page + 1) * size * 10));

    ApiFuture<QuerySnapshot> future = db.collection("posts")
            .whereEqualTo("isPublic", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(fetchLimit)
            .get();

    List<Post> filtered = new ArrayList<>();
    for (DocumentSnapshot doc : future.get().getDocuments()) {
        Post p = doc.toObject(Post.class);
        if (p == null) continue;
        p.setPostId(doc.getId());

        String title = p.getTitle() == null ? "" : p.getTitle().toLowerCase();
        String content = p.getContent() == null ? "" : p.getContent().toLowerCase();

        boolean match = title.contains(q) || content.contains(q);

        if (!match && p.getTags() != null) {
            for (String tag : p.getTags()) {
                if (tag != null && tag.toLowerCase().contains(q)) {
                    match = true;
                    break;
                }
            }
        }

        if (match) filtered.add(p);
    }

    // Paginate sau khi filter
    int from = page * size;
    if (from >= filtered.size()) return new ArrayList<>();
    int to = Math.min(from + size, filtered.size());
    return filtered.subList(from, to);
}

}
