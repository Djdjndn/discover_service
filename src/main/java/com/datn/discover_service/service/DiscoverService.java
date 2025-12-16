package com.datn.discover_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.CreatePostCommentRequest;
import com.datn.discover_service.dto.CreatePostRequest;
import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.PostDetailResponse;
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
    private TripRepository tripRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

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
    // API 3: Post detail
    // =========================
    public PostDetailResponse getPostDetail(String postId, String currentUserId) throws Exception {

        Post post = postRepository.getPost(postId);
        if (post == null) return null;

        User user = usersRepository.getUser(post.getUserId());

        Trip trip = null;
        if (post.getTripId() != null && !post.getTripId().isBlank()) {
            trip = tripRepository.getTrip(post.getTripId());
        }

        // ----- Likes -----
        long likeCount = postLikeRepository.countByPostId(postId);
        boolean userLiked = false;
        if (currentUserId != null && !currentUserId.isBlank()) {
            userLiked = postLikeRepository.exists(postId, currentUserId);
        }

        // ----- Comments -----
        List<PostComment> comments = postCommentRepository.findByPostId(postId);

        // ================= BUILD RESPONSE =================
        PostDetailResponse resp = new PostDetailResponse();

        // Post
        PostDetailResponse.PostDto postDto = new PostDetailResponse.PostDto();
        postDto.setPostId(post.getPostId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setImages(post.getImages());
        postDto.setTags(post.getTags());
        postDto.setCreatedAt(post.getCreatedAt());
        resp.setPost(postDto);

        // User
        if (user != null) {
            PostDetailResponse.UserDto userDto = new PostDetailResponse.UserDto();
            userDto.setUserId(user.getId());
            userDto.setUserName(user.getFirstName() + " " + user.getLastName());
            userDto.setAvatar(user.getProfilePicture());
            resp.setUser(userDto);
        }

        // Trip
        if (trip != null) {
            PostDetailResponse.TripShortDto tripDto = new PostDetailResponse.TripShortDto();
            tripDto.setTripId(trip.getId());
            tripDto.setTitle(trip.getTitle());
            tripDto.setCoverPhoto(trip.getCoverPhoto());
            resp.setTrip(tripDto);
        }

        // Likes info
        PostDetailResponse.LikesInfoDto likesInfo = new PostDetailResponse.LikesInfoDto();
        likesInfo.setCount(likeCount);
        likesInfo.setUserLiked(userLiked);
        resp.setLikes(likesInfo);

        // Comments
        List<PostDetailResponse.CommentDto> commentDtos = new ArrayList<>();
        for (PostComment c : comments) {

            PostDetailResponse.CommentDto cDto = new PostDetailResponse.CommentDto();
            cDto.setCommentId(c.getId());
            cDto.setUserId(c.getUserId());
            cDto.setContent(c.getContent());
            cDto.setCreatedAt(c.getCreatedAt());

            User cu = usersRepository.getUser(c.getUserId());
            if (cu != null) {
                cDto.setUserName(cu.getFirstName() + " " + cu.getLastName());
                cDto.setAvatar(cu.getProfilePicture());
            }

            commentDtos.add(cDto);
        }
        resp.setComments(commentDtos);

        return resp;
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
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setImages(req.getImages());
        post.setTags(req.getTags());
        post.setTripId(req.getTripId());
        post.setCreatedAt(System.currentTimeMillis());
        post.setIsPublic(true);
        post.setLikesCount(0);
        post.setCommentsCount(0);

        postRepository.createPost(post);
        return postId;
    }

    // =========================
    // API: Like / Unlike
    // =========================
    public void likePost(String postId, String userId) throws Exception {
        if (!postLikeRepository.exists(postId, userId)) {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(System.currentTimeMillis());
            postLikeRepository.like(like);
        }
    }

    public void unlikePost(String postId, String userId) throws Exception {
        postLikeRepository.unlike(postId, userId);
    }

    // =========================
    // API: Comment
    // =========================
    public void addPostComment(String postId, CreatePostCommentRequest req) {

        PostComment c = new PostComment();
        c.setPostId(postId);
        c.setUserId(req.getUserId());
        c.setContent(req.getContent());
        c.setCreatedAt(System.currentTimeMillis());
        c.setUserName(req.getUserName());
        c.setAvatar(req.getAvatar());

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
            item.setTitle(post.getTitle());
            item.setCoverPhoto(post.getCoverPhoto());
            item.setCreatedAt(post.getCreatedAt());
            item.setTags(post.getTags());
            item.setLikesCount(likeCount);
            item.setCommentsCount(commentCount);

            if (user != null) {
                item.setUserId(user.getId());
                item.setUserName(user.getFirstName() + " " + user.getLastName());
                item.setUserAvatar(user.getProfilePicture());
            }

            result.add(item);
        }

        return result;
    }
    // =========================
    // API 4: Search bài viết
    // =========================
    public List<DiscoverItem> search(String query, int page, int size) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String q = query.trim();

        // Search theo text (title/content/tags) - phía repo sẽ filter
        List<Post> posts = postRepository.searchPublicPostsFlexible(q, page, size);
        return mapPostsToDiscoverItems(posts);
    }

    // =========================
    // API 5: Filter theo địa điểm
    // (Bạn đang có endpoint ở Controller nên Service cần có hàm để compile)
    // =========================
    public List<DiscoverItem> filterByLocation(double lat, double lng, double radiusKm, int page, int size) throws Exception {
        // Firestore không hỗ trợ geo query “bán kính” trực tiếp nếu bạn chưa setup geohash.
        // Tạm thời trả empty để hệ thống compile & chạy ổn.
        return new ArrayList<>();
}
}
