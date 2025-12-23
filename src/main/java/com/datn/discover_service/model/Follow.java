package com.datn.discover_service.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    private String followerId; // Firebase UID
    private String followingId; // Firebase UID
    private LocalDateTime createdAt;
}