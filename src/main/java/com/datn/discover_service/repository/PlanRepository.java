package com.datn.discover_service.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.Plan;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

@Repository
public class PlanRepository {

    @Autowired
    private Firestore db;

    /* =========================
       EXISTING (GIỮ NGUYÊN)
       ========================= */
    public List<Plan> getAllPlans() throws Exception {
        ApiFuture<QuerySnapshot> future = db.collection("plans").get();
        List<Plan> list = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Plan p = doc.toObject(Plan.class);
            list.add(p);
        }
        return list;
    }

    /* =========================
       ADD: FIND BY ID
       ========================= */
    public Optional<Plan> findById(String planId) throws Exception {
        DocumentReference ref = db.collection("plans").document(planId);
        DocumentSnapshot snapshot = ref.get().get();

        if (!snapshot.exists()) {
            return Optional.empty();
        }

        Plan plan = snapshot.toObject(Plan.class);
        plan.setId(snapshot.getId()); // 🔥 RẤT QUAN TRỌNG
        return Optional.of(plan);
    }

    /* =========================
       ADD: SAVE (UPDATE PLAN)
       ========================= */
    public void save(Plan plan) throws Exception {
        if (plan.getId() == null) {
            throw new IllegalArgumentException("Plan ID must not be null");
        }
        db.collection("plans")
          .document(plan.getId())
          .set(plan, SetOptions.merge())
          .get();
    }
}
