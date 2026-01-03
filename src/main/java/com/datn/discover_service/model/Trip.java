package com.datn.discover_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IgnoreExtraProperties
public class Trip {

    private String id;
    private String userId;
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Builder.Default
    private String isPublic = "none"; 
    // values: none | public | follower

    private String coverPhoto;

    /** Nội dung chia sẻ khi post lên feed */
    private String content;

    /** Tag dạng chuỗi hoặc JSON string */
    private String tags;

    /** Danh sách plan thuộc trip */
    private List<Plan> plans;

    /** Danh sách thành viên tham gia trip */
    private List<User> members;

    /** Danh sách user được share khi isPublic = follower */
    private List<User> sharedWithUsers;

    /** Thời điểm tạo trip */
    private LocalDateTime createdAt;

    /** Thời điểm share trip lên feed */
    private LocalDateTime sharedAt;
}
