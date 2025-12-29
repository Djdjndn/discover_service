package com.datn.discover_service.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    private String id;
    private String userId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Builder.Default
    private String isPublic = "none"; // Values: "none", "public", "follower"
    
    private String coverPhoto;
    private String content; // User's feelings/review about their trip
    private String tags; // JSON array: ["food", "beach", "adventure"] for categorization
    private List<Plan> plans;
    private Timestamp  createdAt;
    private Timestamp    sharedAt; // Timestamp when trip was first shared
    @Builder.Default
    private Integer likeCount = 0;

}
