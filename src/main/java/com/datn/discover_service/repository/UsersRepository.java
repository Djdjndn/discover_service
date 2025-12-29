package com.datn.discover_service.repository;

import java.util.List;

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
    public List<User> searchUsers(String keyword) {
        try {
            return db.collection("users")
                    .get()
                    .get()
                    .toObjects(User.class)
                    .stream()
                    .filter(u ->
                            contains(u.getFirstName(), keyword)
                            || contains(u.getLastName(), keyword)
                    )
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private boolean contains(String source, String kw) {
        return source != null && source.toLowerCase().contains(kw);
    }
}

