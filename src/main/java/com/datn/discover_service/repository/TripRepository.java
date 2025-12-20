package com.datn.discover_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.Trip;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

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

        Trip trip = new Trip();

        trip.setId(doc.getId());
        trip.setTitle(doc.getString("title"));
        trip.setCoverPhoto(doc.getString("coverPhoto"));

        // ❌ KHÔNG đọc startDate / endDate
        // ❌ KHÔNG toObject()

        return trip;
    }

}
