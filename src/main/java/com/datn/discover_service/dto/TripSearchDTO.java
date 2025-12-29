package com.datn.discover_service.dto;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TripSearchDTO {

    private String id;
    private String title;
    private String content;
    private String coverPhoto;
    private String tags;
    private String userId;
    private Timestamp sharedAt;
    private Integer likeCount;
}
