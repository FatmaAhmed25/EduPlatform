package com.edu.eduplatform;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Objects;

@SpringBootApplication
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class EduPlatformApplication  {

    public static void main(String[] args) throws IOException {
        initializeFirebase();
        SpringApplication.run(EduPlatformApplication.class, args);
    }

    private static void initializeFirebase() throws IOException {
        // Check if Firebase is already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            // Use getResourceAsStream to load the file from classpath
            InputStream serviceAccount = EduPlatformApplication.class.getClassLoader()
                    .getResourceAsStream("serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("serviceAccountKey.json file not found in resources folder");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Initialize the default Firebase app
            FirebaseApp.initializeApp(options);
        } else {
            System.out.println("Firebase app already initialized.");
        }
    }
}
