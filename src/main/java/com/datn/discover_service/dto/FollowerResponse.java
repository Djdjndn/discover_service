package com.datn.discover_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerResponse {
    private String userId;
    private String userName;
    private String avatarUrl;
}