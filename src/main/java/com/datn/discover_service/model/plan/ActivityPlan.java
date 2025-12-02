package com.datn.discover_service.model.plan;

import java.time.LocalDateTime;

import com.datn.discover_service.model.Plan;

public class ActivityPlan extends Plan {
    private LocalDateTime endTime;
    
    public ActivityPlan() {
        super();
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}