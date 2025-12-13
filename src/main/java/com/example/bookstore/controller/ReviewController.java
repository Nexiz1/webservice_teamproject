package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.review.MyReviewsResponse;
import com.example.bookstore.dto.review.ReviewRequest;
import com.example.bookstore.dto.review.ReviewResponse;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 22. POST /api/books/{bookId}/reviews - 리뷰 작성
    @Operation(summary = "리뷰 작성", description = "도서에 리뷰를 작성합니다")
    @PostMapping("/books/{bookId}/reviews")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createReview(
            @AuthenticationPrincipal User user,
            @PathVariable("bookId") Long bookId,
            @Valid @RequestBody ReviewRequest request) {
        Long reviewId = reviewService.createReview(user, bookId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 등록되었습니다", Map.of(
                "reviewId", reviewId,
                "createdAt", LocalDateTime.now()
        )));
    }

    // 23. GET /api/reviews/me - 내 리뷰 전체 조회
    @Operation(summary = "내 리뷰 전체 조회", description = "현재 사용자가 작성한 모든 리뷰를 조회합니다")
    @GetMapping("/reviews/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<MyReviewsResponse>> getMyReviews(@AuthenticationPrincipal User user) {
        MyReviewsResponse response = reviewService.getMyReviews(user);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 24. GET /api/reviews/{reviewId} - 리뷰 단건 조회 (공개)
    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 리뷰 정보를 조회합니다")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(@PathVariable("reviewId") Long reviewId) {
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 25. GET /api/books/{bookId}/reviews - 도서별 리뷰 조회
    @Operation(summary = "도서별 리뷰 조회", description = "특정 도서의 모든 리뷰를 조회합니다")
    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getBookReviews(@PathVariable("bookId") Long bookId) {
        List<ReviewResponse> response = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 26. PATCH /api/reviews/{reviewId} - 리뷰 수정
    @Operation(summary = "리뷰 수정", description = "자신의 리뷰를 수정합니다")
    @PatchMapping("/reviews/{reviewId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateReview(
            @AuthenticationPrincipal User user,
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        LocalDateTime updatedAt = reviewService.updateReview(user, reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 수정되었습니다", Map.of(
                "reviewId", reviewId,
                "updatedAt", updatedAt
        )));
    }

    // 27. DELETE /api/reviews/{reviewId} - 리뷰 삭제
    @Operation(summary = "리뷰 삭제", description = "자신의 리뷰를 삭제합니다")
    @DeleteMapping("/reviews/{reviewId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(user, reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 삭제되었습니다"));
    }
}
