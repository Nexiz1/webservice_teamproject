package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.order.OrderResponse;
import com.example.bookstore.entity.Order;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order (Admin)", description = "주문 관리자 API")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

    private final OrderService orderService;

    // 35. GET /api/admin/orders - 전체 주문 목록 조회 (관리자)
    @Operation(summary = "전체 주문 목록 조회", description = "전체 주문 목록을 조회합니다 (관리자 전용)")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<OrderResponse> response = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 36. GET /api/admin/orders/status/{status} - 상태별 주문 조회 (관리자)
    @Operation(summary = "상태별 주문 조회", description = "특정 상태의 주문 목록을 조회합니다 (관리자 전용)")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(@PathVariable("status") Order.OrderStatus status) {
        List<OrderResponse> response = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }
}
