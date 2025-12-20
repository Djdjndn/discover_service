package com.datn.discover_service.dto;

public class TripResponseDto {
    private boolean success;
    private Object trip;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public Object getTrip() {
        return trip;
    }
    public void setTrip(Object trip) {
        this.trip = trip;
    }
}

