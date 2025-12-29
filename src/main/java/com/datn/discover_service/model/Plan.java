package com.datn.discover_service.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    private String id;
    private String tripId; // Reference to Trip
    private String title;
    private String address;
    private String location;
    private String startTime;
    private Double expense;
    private String photoUrl; // Main photo (single image)
    private List<String> photos = new ArrayList<>(); // Collection of photos (multiple images) - stores filenames
    private PlanType type;
    private List<PlanLike> likes;
    private List<PlanComment> comments;
    private String createdAt;
    private long likeCount;  
    // Explicit setter to ensure it works with subclasses
    public void setPhotos(List<String> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }
}
