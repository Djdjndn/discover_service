package com.datn.discover_service.service;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import com.datn.discover_service.dto.CreatePostCommentRequest;
    import com.datn.discover_service.dto.CreatePostRequest;
    import com.datn.discover_service.dto.DiscoverItem;
    import com.datn.discover_service.dto.PostResponse;
    import com.datn.discover_service.model.Post;
    import com.datn.discover_service.model.PostComment;
    import com.datn.discover_service.model.PostLike;
    import com.datn.discover_service.model.Trip;
    import com.datn.discover_service.model.User;
    import com.datn.discover_service.repository.FollowRepository;
    import com.datn.discover_service.repository.PostCommentRepository;
    import com.datn.discover_service.repository.PostLikeRepository;
    import com.datn.discover_service.repository.PostRepository;
    import com.datn.discover_service.repository.TripRepository;
    import com.datn.discover_service.repository.UsersRepository;

    @Service
    public class DiscoverService {

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private UsersRepository usersRepository;

        @Autowired
        private FollowRepository followRepository;

        @Autowired
        private PostLikeRepository postLikeRepository;

        @Autowired
        private PostCommentRepository postCommentRepository;

        @Autowired
        private TripRepository tripRepository;


        // =========================
        // API 1: Discover chung
        // =========================
        public List<DiscoverItem> getDiscoverList(int page, int size, String sort) throws Exception {
            List<Post> posts = postRepository.getPublicPosts(page, size, sort);
            return mapPostsToDiscoverItems(posts);
        }

        // =========================
        // API 2: Discover following
        // =========================
        public List<DiscoverItem> getDiscoverListFollowing(String userId, int page, int size) throws Exception {
            List<String> followingIds = followRepository.getFollowingIds(userId);
            if (followingIds.isEmpty()) return new ArrayList<>();

            List<Post> posts = postRepository.getPostsByUserIds(followingIds, page, size);
            return mapPostsToDiscoverItems(posts);
        }

        // =========================
        // API 3: Build PostResponse (Facebook style)
        // =========================
        public PostResponse buildPostResponse(String postId, String currentUserId) throws Exception {

            Post post = postRepository.getPost(postId);
            if (post == null) {
                throw new RuntimeException("Post not found: " + postId);
            }

            User user = usersRepository.getUser(post.getUserId());
            String tripImage = null;
            String tripName = null;
            long likeCount = postLikeRepository.countByPostId(postId);
            long commentCount = postCommentRepository.countByPostId(postId);

            boolean isLiked = false;
            if (currentUserId != null && !currentUserId.isBlank()) {
                isLiked = postLikeRepository.exists(postId, currentUserId);
            }
            if (post.getTripId() != null) {
                Trip trip = tripRepository.getTrip(post.getTripId());
                if (trip != null) {
                    tripImage = trip.getCoverPhoto();
                    tripName = trip.getTitle();
                }
            }
            return new PostResponse(
                    post.getPostId(),
                    post.getContent(),
                    Boolean.TRUE.equals(post.getIsPublic()),
                    post.getCreatedAt(),

                    user != null ? user.getId() : null,
                    user != null ? user.getFirstName() + " " + user.getLastName() : null,
                    user != null ? user.getProfilePicture() : null,

                    post.getTripId(),
                    tripName,
                    tripImage,  

                    likeCount,
                    commentCount,
                    isLiked
            );
        }

        // =========================
        // API: Create Post
        // =========================
        public String createPost(CreatePostRequest req) throws Exception {

            User user = usersRepository.getUser(req.getUserId());
            if (user == null) throw new Exception("User not found");

            String postId = UUID.randomUUID().toString();

            Post post = new Post();
            post.setPostId(postId);
            post.setUserId(req.getUserId());
            post.setTripId(req.getTripId());
            post.setTitle(req.getTitle());
            post.setContent(req.getContent());
            post.setImages(req.getImages());
            post.setTags(req.getTags());
            post.setCreatedAt(System.currentTimeMillis());
            post.setIsPublic(true); // 🔥 giữ logic của bạn

            postRepository.createPost(post);
            return postId;
        }

        // =========================
        // API: Like / Unlike
        // =========================
        public void likePost(String postId, String userId) throws Exception {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(System.currentTimeMillis());
            postLikeRepository.like(like); // set() → ghi đè
        }

        public void unlikePost(String postId, String userId) {
            try {
                postLikeRepository.unlike(postId, userId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to unlike post", e);
            }
}

        // =========================
        // API: Comment
        // =========================
        public void addPostComment(String postId, CreatePostCommentRequest req) throws Exception {

            PostComment c = new PostComment();
            c.setPostId(postId);
            c.setUserId(req.getUserId());
            c.setContent(req.getContent());
            c.setCreatedAt(System.currentTimeMillis());
            User user = usersRepository.getUser(req.getUserId());
            if (usersRepository != null) {
            String displayName = "";

            if (user.getFirstName() != null) {
                displayName += user.getFirstName();
            }
            if (user.getLastName() != null) {
                if (!displayName.isEmpty()) displayName += " ";
                displayName += user.getLastName();
            }

            if (displayName.isEmpty()) {
                displayName = "Ẩn danh";
            }

            c.setUserName(displayName);
            c.setAvatar(user.getProfilePicture());
        } else {
            c.setUserName("Ẩn danh");
            c.setAvatar(null);
        }

            postCommentRepository.save(c);
        }

        public List<PostComment> getPostComments(String postId) throws Exception {
            return postCommentRepository.findByPostId(postId);
        }

        // =========================
        // Helper
        // =========================
        private List<DiscoverItem> mapPostsToDiscoverItems(List<Post> posts) throws Exception {

        List<DiscoverItem> result = new ArrayList<>();

        for (Post post : posts) {

            User user = usersRepository.getUser(post.getUserId());

            long likeCount = postLikeRepository.countByPostId(post.getPostId());
            long commentCount = postCommentRepository.countByPostId(post.getPostId());

            DiscoverItem item = new DiscoverItem();

            item.setPostId(post.getPostId());
            item.setCaption(post.getContent());
            item.setLikesCount(likeCount);
            item.setCommentsCount(commentCount);
            item.setCreatedAt(post.getCreatedAt());

            if (user != null) {
                item.setUserId(user.getId());
                item.setUserName(user.getFirstName() + " " + user.getLastName());
                item.setUserAvatar(user.getProfilePicture());
            }

            // ✅ FIX CHUẨN: lấy ảnh từ Trip
            if (post.getTripId() != null) {
                Trip trip = tripRepository.getTrip(post.getTripId());
                if (trip != null) {
                    item.setTripImage(trip.getCoverPhoto());
                }
            }

            result.add(item);
        }

        return result;
    }

        // =========================
        // API: Search
        // =========================
        public List<DiscoverItem> search(String query, int page, int size) throws Exception {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }
            String q = query.trim();
            List<Post> posts = postRepository.searchPublicPostsFlexible(q, page, size);
            return mapPostsToDiscoverItems(posts);
        }

        // =========================
        // API: Filter (stub)
        // =========================
        public List<DiscoverItem> filterByLocation(
                double lat,
                double lng,
                double radiusKm,
                int page,
                int size
        ) {
            return new ArrayList<>();
        }
    }