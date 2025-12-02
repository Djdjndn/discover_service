package com.datn.discover_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.DiscoverItem;
import com.datn.discover_service.dto.PostDetailResponse;
import com.datn.discover_service.model.Comment;
import com.datn.discover_service.model.Plan;
import com.datn.discover_service.model.Post;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.CommentRepository;
import com.datn.discover_service.repository.FollowRepository;
import com.datn.discover_service.repository.LikeRepository;
import com.datn.discover_service.repository.PlanRepository;
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
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired(required = false)
    private PlanRepository planRepository; // chỉ dùng cho filter theo location nếu bạn cần

    // -------- API 1: Discover list chung --------
    public List<DiscoverItem> getDiscoverList(int page, int size, String sort) throws Exception {
        List<Post> posts = postRepository.getPublicPosts(page, size, sort);
        return mapPostsToDiscoverItems(posts);
    }

    // -------- API 2: Discover list chỉ bài của người mình follow --------
    public List<DiscoverItem> getDiscoverListFollowing(String userId, int page, int size) throws Exception {
        List<String> followingIds = followRepository.getFollowingIds(userId);
        if (followingIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Post> posts = postRepository.getPostsByUserIds(followingIds, page, size);
        return mapPostsToDiscoverItems(posts);
    }

    // -------- API 3: Chi tiết bài viết --------
    public PostDetailResponse getPostDetail(String postId, String currentUserId) throws Exception {
        Post post = postRepository.getPost(postId);
        if (post == null) return null;

        User user = usersRepository.getUser(post.getUserId());
        Trip trip = null;
        if (post.getTripId() != null) {
            trip = tripRepository.getTrip(post.getTripId());
        }
        List<Comment> comments = commentRepository.getComments(postId);

        int likeCount = likeRepository.getLikeCount(postId);
        boolean userLiked = false;
        if (currentUserId != null && !currentUserId.isBlank()) {
            userLiked = likeRepository.isUserLiked(postId, currentUserId);
        }

        // build response
        PostDetailResponse resp = new PostDetailResponse();

        // post
        PostDetailResponse.PostDto postDto = new PostDetailResponse.PostDto();
        postDto.setPostId(post.getPostId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setImages(post.getImages());
        postDto.setTags(post.getTags());
        postDto.setCreatedAt(post.getCreatedAt());
        resp.setPost(postDto);

        // user
        if (user != null) {
            PostDetailResponse.UserDto userDto = new PostDetailResponse.UserDto();
            userDto.setUserId(user.getId());
            userDto.setUserName(user.getFirstName() + " " + user.getLastName());
            userDto.setAvatar(user.getProfilePicture());
            resp.setUser(userDto);
        }

        // trip short
        if (trip != null) {
            PostDetailResponse.TripShortDto tripDto = new PostDetailResponse.TripShortDto();
            tripDto.setTripId(trip.getId());
            tripDto.setTitle(trip.getTitle());
            tripDto.setCoverPhoto(trip.getCoverPhoto());
            resp.setTrip(tripDto);
        }

        // likes
        PostDetailResponse.LikesInfoDto likesInfo = new PostDetailResponse.LikesInfoDto();
        likesInfo.setCount(likeCount);
        likesInfo.setUserLiked(userLiked);
        resp.setLikes(likesInfo);

        // comments
        List<PostDetailResponse.CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            PostDetailResponse.CommentDto cDto = new PostDetailResponse.CommentDto();
            cDto.setCommentId(c.getCommentId());
            cDto.setUserId(c.getUserId());
            cDto.setContent(c.getContent());
            cDto.setCreatedAt(c.getCreatedAt());
            // nếu muốn thêm userName/avatar cho comment thì phải join thêm user
            commentDtos.add(cDto);
        }
        resp.setComments(commentDtos);

        return resp;
    }

    // -------- API 4: Search --------
    public List<DiscoverItem> search(String queryText, int page, int size) throws Exception {
        List<Post> posts = postRepository.searchPublicPosts(queryText, page, size);
        return mapPostsToDiscoverItems(posts);
    }

    // -------- API 5: Filter theo location (version đơn giản) --------
    public List<DiscoverItem> filterByLocation(double lat, double lng, double radiusKm,
                                               int page, int size) throws Exception {
        if (planRepository == null) {
            return new ArrayList<>();
        }

        // Version đơn giản: load tất cả plans, filter trong Java
        // Version đơn giản: load tất cả plans, filter trong Java
        List<Plan> allPlans = planRepository.getAllPlans();
        List<String> tripIds = new ArrayList<>();

        for (Plan p : allPlans) {

            String loc = p.getLocation();    // VD: "21.04,105.88"
            if (loc == null || !loc.contains(",")) continue;

            // Tách lat & lng
            String[] parts = loc.split(",");
            if (parts.length != 2) continue;

            double planLat;
            double planLng;

            try {
                planLat = Double.parseDouble(parts[0].trim());
                planLng = Double.parseDouble(parts[1].trim());
            } catch (Exception e) {
                continue;
            }

            double d = distanceKm(lat, lng, planLat, planLng);

            if (d <= radiusKm && p.getTripId() != null) {
                if (!tripIds.contains(p.getTripId())) {
                    tripIds.add(p.getTripId());
                }
            }
        }


        if (tripIds.isEmpty()) return new ArrayList<>();

        // lấy tất cả posts thuộc các trip này
        // (ở đây làm đơn giản: load nhiều rồi filter, tuỳ bạn tối ưu)
        List<Post> matchedPosts = new ArrayList<>();
        List<Post> publicPosts = postRepository.getPublicPosts(page, size, "newest");
        for (Post post : publicPosts) {
            if (post.getTripId() != null && tripIds.contains(post.getTripId())) {
                matchedPosts.add(post);
            }
        }

        return mapPostsToDiscoverItems(matchedPosts);
    }

    // -------- Helper: map Post -> DiscoverItem --------
    private List<DiscoverItem> mapPostsToDiscoverItems(List<Post> posts) throws Exception {
        List<DiscoverItem> result = new ArrayList<>();
        for (Post post : posts) {
            User user = usersRepository.getUser(post.getUserId());
            int likeCount = post.getLikesCount() != null ? post.getLikesCount() : likeRepository.getLikeCount(post.getPostId());
            int commentCount = post.getCommentsCount() != null ? post.getCommentsCount() : commentRepository.getCommentCount(post.getPostId());

            DiscoverItem item = new DiscoverItem();
            item.setPostId(post.getPostId());
            item.setTitle(post.getTitle());
            item.setCoverPhoto(post.getCoverPhoto());
            item.setCreatedAt(post.getCreatedAt());
            item.setTags(post.getTags());

            if (user != null) {
                item.setUserId(user.getId());
                item.setUserName(user.getFirstName() + " " + user.getLastName());
                item.setUserAvatar(user.getProfilePicture());
            }

            item.setLikesCount(likeCount);
            item.setCommentsCount(commentCount);

            result.add(item);
        }
        return result;
    }

    // -------- Helper: tính khoảng cách Haversine --------
    private double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) *
                        Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
