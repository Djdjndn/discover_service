package com.datn.discover_service.model;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Plan {

    private String id;
    private String tripId;

    private String title;
    private String address;
    private String location;

    /** Thời gian bắt đầu plan */
    private String startTime;

    private Double expense;

    /** Ảnh đại diện plan */
    private String photoUrl;

    /** Danh sách ảnh */
    private List<String> photos = new ArrayList<>();

    private PlanType type;

    private List<PlanLike> likes;
    private List<PlanComment> comments = new ArrayList<>();

    private String createdAt;
    private long likeCount;

    /** đảm bảo không null */
    public void setPhotos(List<String> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }
}
