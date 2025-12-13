package com.example.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Health", description = "헬스체크 API")
@RestController
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    // 37. GET /health - 헬스체크
    @Operation(summary = "헬스체크", description = "서버 상태를 확인합니다")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", applicationName,
                "version", "1.0.0",
                "timestamp", LocalDateTime.now()
        ));
    }
}
