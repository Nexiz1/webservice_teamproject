package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.auth.*;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. POST /api/users - 회원가입
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Long>>> signUp(@Valid @RequestBody SignUpRequest request) {
        Long userId = authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success("유저를 생성했습니다", Map.of("userId", userId)));
    }

    // 2. POST /api/auth/login - 로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    // 3. POST /api/auth/refresh - 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급합니다")
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 재발급 성공", response));
    }

    // 4. POST /api/auth/logout - 로그아웃
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다")
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        authService.logout(user);
        return ResponseEntity.ok().build();
    }

    // 5. POST /api/auth/firebase - Firebase 로그인
    @Operation(summary = "Firebase 로그인", description = "Firebase ID Token을 사용하여 로그인합니다")
    @PostMapping("/auth/firebase")
    public ResponseEntity<ApiResponse<LoginResponse>> firebaseLogin(@Valid @RequestBody FirebaseLoginRequest request) {
        LoginResponse response = authService.firebaseLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Firebase 로그인 성공", response));
    }
}
