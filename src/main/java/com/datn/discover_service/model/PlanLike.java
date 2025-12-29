package com.datn.discover_service.model;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanLike {

    private Long id;
    private Long planId; // Reference to Plan
    private String userId; // Reference to User (Firebase UID)
    private Timestamp  createdAt;
}
