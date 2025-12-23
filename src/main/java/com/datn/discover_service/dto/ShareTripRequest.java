package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ShareTripRequest  {
    private String tripId;
    private String content;
    private String isPublic; // public | follower
    private String tags;
}
