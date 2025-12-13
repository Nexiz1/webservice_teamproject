package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.favorite.FavoriteRequest;
import com.example.bookstore.dto.favorite.FavoriteResponse;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Favorite", description = "찜 API")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 28. POST /api/favorites - 찜 등록
    @Operation(summary = "찜 등록", description = "도서를 찜 목록에 추가합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> addFavorite(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FavoriteRequest request) {
        Long favoriteId = favoriteService.addFavorite(user, request);
        return ResponseEntity.ok(ApiResponse.success("찜 목록에 추가되었습니다", Map.of("favoriteId", favoriteId)));
    }

    // 29. GET /api/favorites - 찜 목록 조회
    @Operation(summary = "찜 목록 조회", description = "현재 사용자의 찜 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getFavorites(@AuthenticationPrincipal User user) {
        List<FavoriteResponse> response = favoriteService.getFavorites(user);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 30. DELETE /api/favorites/{favoriteId} - 찜 삭제
    @Operation(summary = "찜 삭제", description = "찜 목록에서 도서를 삭제합니다")
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable("favoriteId") Long favoriteId) {
        favoriteService.deleteFavorite(user, favoriteId);
        return ResponseEntity.ok(ApiResponse.success("찜이 삭제되었습니다"));
    }
}
