package com.datn.discover_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id; // Firebase UID
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Nullable for OAuth users

    @Builder.Default
    private String role = "user"; // guest | user | admin

    private String profilePicture;
    private AuthProvider provider;
    private String providerId; // Google ID, Facebook ID, etc.

    @Builder.Default
    private Boolean enabled = true;

    // Firestore đang lưu dạng number (epoch millis)
    private Long createdAt;
    private Long updatedAt;
    
}