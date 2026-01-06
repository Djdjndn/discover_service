package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ShareTripRequest  {
    private String tripId;
    private String content;
    private String isPublic; // PUBLIC | FOLLOWER | MEMBER
    private String tags;
    private List<String> sharedWithUsers;
}
