package com.datn.discover_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.PlanDetailResponse;
import com.datn.discover_service.model.Plan;
import com.datn.discover_service.model.PlanComment;
import com.datn.discover_service.model.PlanLike;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.repository.PlanRepository;
import com.datn.discover_service.repository.TripRepository;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final TripRepository tripRepository;

    public PlanService(PlanRepository planRepository, TripRepository tripRepository) {
        this.planRepository = planRepository;
        this.tripRepository = tripRepository;
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

    public List<PlanComment> getComments(String planId) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            return plan.getComments() != null ? plan.getComments() : List.of();

        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments", e);
        }
    }

    public void addComment(String planId, String userId, String content) {
        try {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Plan not found"));

            if (plan.getComments() == null) {
                plan.setComments(new ArrayList<>());
            }

            // ✅ FIX: createdAt để null (tránh serialize LocalDateTime)
            plan.getComments().add(
                    PlanComment.builder()
                            .id(System.currentTimeMillis())
                            .planId(Long.parseLong(planId))
                            .userId(userId)
                            .content(content)
                            .createdAt(null)
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
}
