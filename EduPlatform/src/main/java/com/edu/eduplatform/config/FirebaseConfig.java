package com.edu.eduplatform.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    private static final String FIREBASE_APP_NAME = "edu-platform-app"; // Unique Firebase app name

    @Value("${bucket.name}")
    private String bucketName;

    @Bean
    public FirebaseApp initializeFirebaseApp() throws IOException {
        // Load Firebase credentials from a service account JSON file
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(bucketName) // Use the non-static field here
                .build();

        // Check if FirebaseApp with the specified name already exists
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options, FIREBASE_APP_NAME);
        }

        // Return the initialized FirebaseApp
        return FirebaseApp.getInstance(FIREBASE_APP_NAME);
    }
}