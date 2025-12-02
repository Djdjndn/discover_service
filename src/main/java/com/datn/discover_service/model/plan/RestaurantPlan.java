package com.datn.discover_service.model.plan;
import java.time.LocalDateTime;

import com.datn.discover_service.model.Plan;

public class RestaurantPlan extends Plan {
    private LocalDateTime reservationDate;
    private LocalDateTime reservationTime;
    
    public RestaurantPlan() {
        super();
    }
    
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }
    
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }
}
