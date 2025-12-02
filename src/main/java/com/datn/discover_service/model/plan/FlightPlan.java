package com.datn.discover_service.model.plan;

import java.time.LocalDateTime;

import com.datn.discover_service.model.Plan;

public class FlightPlan extends Plan {
    private String arrivalLocation;
    private String arrivalAddress;
    private LocalDateTime arrivalDate;
    
    public FlightPlan() {
        super();
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
    
    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }
    
    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}
