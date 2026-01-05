package com.datn.discover_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.discover_service.dto.CommentDto;
import com.datn.discover_service.dto.CommentRequest;
import com.datn.discover_service.dto.PlanDetailResponse;
import com.datn.discover_service.service.PlanService;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/{planId}")
    public PlanDetailResponse getPlanDetail(
            @PathVariable String planId,
            @RequestHeader("X-USER-ID") String userId
    ) {
        return planService.getPlanDetail(planId, userId);
    }

    @PostMapping("/{planId}/like")
    public PlanDetailResponse likePlan(
            @PathVariable String planId,
            @RequestHeader("X-USER-ID") String userId
    ) {
        return planService.likePlan(planId, userId);
    }

    @DeleteMapping("/{planId}/like")
    public PlanDetailResponse unlikePlan(
            @PathVariable String planId,
            @RequestHeader("X-USER-ID") String userId
    ) {
        return planService.unlikePlan(planId, userId);
    }

    @GetMapping("/{planId}/comments")
    public List<CommentDto> getComments(@PathVariable String planId) {
        return planService.getComments(planId);
    }

    @PostMapping("/{planId}/comments")
    public void postComment(
            @PathVariable String planId,
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody CommentRequest request
    ) {
        planService.addComment(planId, userId, request.getContent(), request.getParentId());
    }
}
