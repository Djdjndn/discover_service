package com.datn.discover_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.CommentDto;
import com.datn.discover_service.dto.PlanDetailResponse;
import com.datn.discover_service.model.Plan;
import com.datn.discover_service.model.PlanComment;
import com.datn.discover_service.model.PlanLike;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.PlanRepository;
import com.datn.discover_service.repository.TripRepository;
import com.datn.discover_service.repository.UsersRepository;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final TripRepository tripRepository;
    private final UsersRepository usersRepository;

    public PlanService(PlanRepository planRepository, TripRepository tripRepository, UsersRepository usersRepository) {
        this.planRepository = planRepository;
        this.tripRepository = tripRepository;
        this.usersRepository = usersRepository;
    }

    public PlanDetailResponse getPlanDetail(String planId, String userId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            List<PlanLike> likes = plan.getLikes() != null ? plan.getLikes() : new ArrayList<>();
            List<PlanComment> comments = plan.getComments() != null ? plan.getComments() : new ArrayList<>();

            boolean isLiked = likes.stream()
                    .anyMatch(like -> like.getUserId().equals(userId));
            
            PlanDetailResponse res = new PlanDetailResponse();
            res.setPlanId(plan.getId());
            res.setTitle(plan.getTitle());
            res.setAddress(plan.getAddress());
            res.setLocation(plan.getLocation());
            res.setExpense(plan.getExpense());
            res.setStartTime(plan.getStartTime() != null ? plan.getStartTime().toString() : null);

            if (plan.getPhotos() != null && !plan.getPhotos().isEmpty()) {
                res.setImages(plan.getPhotos());
            } else if (plan.getPhotoUrl() != null) {
                res.setImages(List.of(plan.getPhotoUrl()));
            } else {
                res.setImages(List.of());
            }

            res.setLikeCount(likes.size());
            res.setCommentCount(comments.size());
            res.setLiked(isLiked);

            return res;

        } catch (Exception e) {
            throw new RuntimeException("Failed to get plan detail", e);
        }
    }

    public PlanDetailResponse likePlan(String planId, String userId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            if (plan.getLikes() == null) {
                plan.setLikes(new ArrayList<>());
            }

            boolean alreadyLiked = plan.getLikes()
                    .stream()
                    .anyMatch(l -> l.getUserId().equals(userId));

            if (alreadyLiked) {
                return getPlanDetail(planId, userId);
            }

            // ✅ FIX: createdAt để null để tránh Firestore serialize LocalDateTime (crash IsoChronology)
            plan.getLikes().add(
                    PlanLike.builder()
                            .id(System.currentTimeMillis())
                            .userId(userId)
                            .createdAt(null)
                            .build()
            );

            plan.setLikeCount(plan.getLikeCount() + 1);
            planRepository.save(plan);

            updateTripLike(plan.getTripId(), +1);

            return getPlanDetail(planId, userId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to like plan", e);
        }
    }

    public PlanDetailResponse unlikePlan(String planId, String userId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            if (plan.getLikes() == null) {
                plan.setLikes(new ArrayList<>());
            }

            boolean removed = plan.getLikes()
                    .removeIf(l -> l.getUserId().equals(userId));

            if (!removed) {
                return getPlanDetail(planId, userId);
            }

            plan.setLikeCount(Math.max(0, plan.getLikeCount() - 1));
            planRepository.save(plan);

            updateTripLike(plan.getTripId(), -1);

            return getPlanDetail(planId, userId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to unlike plan", e);
        }
    }

    public List<CommentDto> getComments(String planId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            List<PlanComment> comments = plan.getComments() != null ? plan.getComments() : List.of();
            
            // Convert to CommentDto with user info
            return comments.stream().map(comment -> {
                try {
                    User user = usersRepository.getUser(comment.getUserId());
                    return CommentDto.builder()
                            .id(comment.getId())
                            .planId(comment.getPlanId())
                            .userId(comment.getUserId())
                            .userName(user != null ? (user.getFirstName() + " " + user.getLastName()) : "Unknown User")
                            .userAvatar(user != null ? user.getProfilePicture() : null)
                            .parentId(comment.getParentId())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toDate().getTime() : null)
                            .build();
                } catch (Exception e) {
                    return CommentDto.builder()
                            .id(comment.getId())
                            .planId(comment.getPlanId())
                            .userId(comment.getUserId())
                            .userName("Unknown User")
                            .userAvatar(null)
                            .parentId(comment.getParentId())
                            .content(comment.getContent())
                            .createdAt(System.currentTimeMillis())
                            .build();
                }
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments", e);
        }
    }

    public void addComment(String planId, String userId, String content, String parentId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            if (plan.getComments() == null) {
                plan.setComments(new ArrayList<>());
            }

            // Save with Firestore Timestamp
            plan.getComments().add(
                    PlanComment.builder()
                            .id(System.currentTimeMillis())
                            .planId(planId)
                            .userId(userId)
                            .parentId(parentId)
                            .content(content)
                            .createdAt(com.google.cloud.Timestamp.now())
                            .build()
            );
            
            planRepository.save(plan);

        } catch (Exception e) {
            throw new RuntimeException("Failed to add comment", e);
        }
    }

    private void updateTripLike(String tripId, int delta) throws Exception {

        tripRepository.updateLikeCount(tripId, delta);
    }

    public void deleteComment(String planId, Long commentId, String userId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            if (plan.getComments() == null) return;

            // 🔥 LẤY TRIP → LẤY CHỦ TUS
            Trip trip = tripRepository.getTrip(plan.getTripId());
            if (trip == null) {
                throw new RuntimeException("Trip not found");
            }

            boolean isPlanOwner = userId.equals(trip.getUserId());

            boolean removed = plan.getComments().removeIf(c ->
                c.getId().equals(commentId)
                && (
                    c.getUserId().equals(userId) // người viết comment
                    || isPlanOwner               // 🔥 chủ tus
                )
            );

            if (!removed) {
                throw new RuntimeException("No permission to delete comment");
            }

            planRepository.save(plan);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment", e);
        }
    }
}
