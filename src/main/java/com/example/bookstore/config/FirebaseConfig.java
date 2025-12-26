package com.example.bookstore.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            // 1. serviceAccountKey.json 파일을 읽어옵니다.
            InputStream serviceAccount = new ClassPathResource("firebaseSecret.json").getInputStream();

            // 2. Firebase 옵션을 설정합니다.
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 3. FirebaseApp이 이미 초기화되어 있는지 확인 후 초기화합니다.
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Application has been initialized");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}