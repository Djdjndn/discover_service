package com.datn.discover_service.repository;

import com.datn.discover_service.model.Plan;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PlanRepository {

    @Autowired
    private Firestore db;

    public List<Plan> getAllPlans() throws Exception {
        ApiFuture<QuerySnapshot> future = db.collection("plans").get();
        List<Plan> list = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Plan p = doc.toObject(Plan.class);
            list.add(p);
        }
        return list;
    }
}
