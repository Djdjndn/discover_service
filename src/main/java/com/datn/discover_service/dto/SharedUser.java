package com.datn.discover_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedUser {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String role;
    private boolean enabled; // Removed as per recent changes
}