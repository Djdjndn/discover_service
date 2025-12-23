package com.datn.discover_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponse {
    private String userId;
    private String userName;
    private String userAvatar;
    private long followerCount;
}
