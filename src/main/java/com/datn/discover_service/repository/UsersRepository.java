package com.datn.discover_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.discover_service.model.User;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

@Repository
public class UsersRepository {

    @Autowired
    private Firestore db;

    public User getUser(String userId) throws Exception {
        DocumentSnapshot doc = db.collection("users")
                .document(userId)
                .get()
                .get();
        return doc.exists() ? doc.toObject(User.class) : null;
    }
}
