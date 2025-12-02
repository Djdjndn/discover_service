package com.datn.discover_service.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    private String id;
    private String tripId; // Reference to Trip
    private String title;
    private String address;
    private String location;
    private LocalDateTime startTime;
    private Double expense;
    private String photoUrl;
    private PlanType type;
    private List<PlanLike> likes;
    private List<PlanComment> comments;
    private LocalDateTime createdAt;
}
