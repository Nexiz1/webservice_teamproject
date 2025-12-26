package com.example.bookstore.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    // 1. .env에서 설정한 값을 가져옵니다. (없으면 null)
    @Value("${firebase.service-account:#{null}}")
    private String firebaseConfigJson;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount = null;

            // 2. 환경 변수 값이 있으면 우선 사용 (운영/Docker 환경)
            if (firebaseConfigJson != null && !firebaseConfigJson.isEmpty()) {
                serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));
            }

            // 4. 초기화 진행
            if (serviceAccount != null) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("✅ Firebase Application initialized using " +
                            (firebaseConfigJson != null ? "Environment Variable" : "Local File"));
                }
            } else {
                System.err.println("❌ Warning: No Firebase credentials found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}