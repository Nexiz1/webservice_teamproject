package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.user.UserResponse;
import com.example.bookstore.dto.user.UserUpdateRequest;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // 5. GET /api/users/me - 내 정보 조회
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@AuthenticationPrincipal User user) {
        UserResponse response = userService.getMyInfo(user);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 6. PATCH /api/users/me - 회원 정보 수정
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateMyInfo(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateRequest request) {
        LocalDateTime updatedAt = userService.updateMyInfo(user, request);
        return ResponseEntity.ok(ApiResponse.success("수정 완료", Map.of(
                "userId", user.getId(),
                "updatedAt", updatedAt
        )));
    }
}
