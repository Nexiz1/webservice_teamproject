package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.user.UserResponse;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User (Admin)", description = "사용자 관리자 API")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    // 31. GET /api/admin/users - 전체 사용자 목록 조회 (관리자)
    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자 목록을 조회합니다 (관리자 전용)")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<UserResponse> response;
        if (name != null && !name.isEmpty()) {
            response = userService.searchUsers(name, pageable);
        } else {
            response = userService.getAllUsers(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 32. GET /api/admin/users/{userId} - 사용자 상세 조회 (관리자)
    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 정보를 조회합니다 (관리자 전용)")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 33. PATCH /api/admin/users/{userId}/role - 사용자 권한 변경 (관리자)
    @Operation(summary = "사용자 권한 변경", description = "사용자의 역할을 변경합니다 (관리자 전용)")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUserRole(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> request) {
        User.Role role = User.Role.valueOf(request.get("role"));
        userService.updateUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("권한이 변경되었습니다", Map.of(
                "userId", userId,
                "role", role.name()
        )));
    }

    // 34. DELETE /api/admin/users/{userId} - 사용자 삭제 (관리자)
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다 (관리자 전용)")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자가 삭제되었습니다"));
    }
}
