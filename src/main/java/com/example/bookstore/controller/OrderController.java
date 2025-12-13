package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.order.OrderRequest;
import com.example.bookstore.dto.order.OrderResponse;
import com.example.bookstore.dto.order.OrderStatusRequest;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    // 18. POST /api/orders - 주문 생성
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OrderRequest request) {
        Long orderId = orderService.createOrder(user, request);
        return ResponseEntity.ok(ApiResponse.success("주문이 정상적으로 생성되었습니다", Map.of(
                "orderId", orderId,
                "createdAt", LocalDateTime.now()
        )));
    }

    // 19. GET /api/orders/{orderId} - 주문 조회
    @Operation(summary = "주문 조회", description = "주문 ID로 주문 정보를 조회합니다")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal User user,
            @PathVariable("orderId") Long orderId) {
        OrderResponse response = orderService.getOrder(user, orderId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 20. GET /api/orders - 내 주문 목록 조회
    @Operation(summary = "내 주문 목록 조회", description = "현재 사용자의 주문 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<OrderResponse> response = orderService.getMyOrders(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 21. PATCH /api/orders/{orderId}/status - 주문 상태 업데이트
    @Operation(summary = "주문 상태 변경", description = "주문의 상태를 변경합니다")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateOrderStatus(
            @AuthenticationPrincipal User user,
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody OrderStatusRequest request) {
        LocalDateTime updatedAt = orderService.updateOrderStatus(user, orderId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("상태가 변경되었습니다", Map.of(
                "orderId", orderId,
                "status", request.getStatus().name(),
                "updatedAt", updatedAt
        )));
    }
}
