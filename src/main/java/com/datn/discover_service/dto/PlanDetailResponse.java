package com.datn.discover_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class PlanDetailResponse {

    private String planId;

    private String title;
    private String address;
    private String location;
    private String startTime;
    private Double expense;

    private List<String> images;   // STORY IMAGES

    private int likeCount;
    private int commentCount;
    private boolean liked;

    private boolean isOwner;

}
