package com.datn.discover_service.model.plan;

import java.time.LocalDateTime;

import com.datn.discover_service.model.Plan;

public class CarRentalPlan extends Plan {
    private LocalDateTime pickupDate;
    private LocalDateTime pickupTime;
    private String phone;
    
    public CarRentalPlan() {
        super();
    }
    
    public LocalDateTime getPickupDate() {
        return pickupDate;
    }
    
    public void setPickupDate(LocalDateTime pickupDate) {
        this.pickupDate = pickupDate;
    }
    
    public LocalDateTime getPickupTime() {
        return pickupTime;
    }
    
    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
