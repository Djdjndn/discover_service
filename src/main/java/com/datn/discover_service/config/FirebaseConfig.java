package com.datn.discover_service.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {

    // destroyMethod="" để Spring KHÔNG tự gọi close() cho Firestore
    @Bean(destroyMethod = "")
    public Firestore firestore() throws IOException {

        // Devtools / restart nóng có thể để lại FirebaseApp cũ (Firestore đã bị close)
        // => delete hết app cũ để init mới sạch sẽ
        for (FirebaseApp app : FirebaseApp.getApps()) {
            try {
                app.delete();
            } catch (Exception ignored) {}
        }

        try (InputStream serviceAccount = new ClassPathResource("firebase-service.json").getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}
