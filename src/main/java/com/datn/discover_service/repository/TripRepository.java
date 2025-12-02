package com.datn.discover_service.repository;

import com.datn.discover_service.model.Trip;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TripRepository {

    @Autowired
    private Firestore db;

    public Trip getTrip(String tripId) throws Exception {
        DocumentSnapshot doc = db.collection("trips")
                .document(tripId)
                .get()
                .get();

        if (!doc.exists()) return null;
        Trip trip = doc.toObject(Trip.class);
        trip.setId(doc.getId());
        return trip;
    }
}
