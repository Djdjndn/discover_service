package com.datn.discover_service.model.plan;
import java.time.LocalDateTime;

import com.datn.discover_service.model.Plan;

public class BoatPlan extends Plan {
    private LocalDateTime arrivalTime;
    private String arrivalLocation;
    private String arrivalAddress;
    
    public BoatPlan() {
        super();
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public String getArrivalLocation() {
        return arrivalLocation;
    }
    
    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }
    
    public String getArrivalAddress() {
        return arrivalAddress;
    }
    
    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }
}
