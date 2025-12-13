package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.cart.CartItemRequest;
import com.example.bookstore.dto.cart.CartItemResponse;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.CartService;
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

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequestMapping("/api/carts/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    // 14. POST /api/carts/items - 장바구니 항목 추가
    @Operation(summary = "장바구니 항목 추가", description = "장바구니에 도서를 추가합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> addCartItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemRequest request) {
        Long cartId = cartService.addCartItem(user, request);
        return ResponseEntity.ok(ApiResponse.success("장바구니에 추가되었습니다", Map.of("cartId", cartId)));
    }

    // 15. PUT /api/carts/items - 장바구니 수량 수정
    @Operation(summary = "장바구니 수량 수정", description = "장바구니 항목의 수량을 수정합니다")
    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateCartItemQuantity(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemRequest request) {
        Long cartId = cartService.updateCartItemQuantity(user, request);
        return ResponseEntity.ok(ApiResponse.success("수량이 수정되었습니다", Map.of("cartId", cartId)));
    }

    // 16. GET /api/carts/items - 장바구니 조회
    @Operation(summary = "장바구니 조회", description = "현재 장바구니의 모든 항목을 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItems(@AuthenticationPrincipal User user) {
        List<CartItemResponse> response = cartService.getCartItems(user);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 17. DELETE /api/carts/items/{cartItemId} - 장바구니 항목 삭제
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 항목을 삭제합니다")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable("cartItemId") Long cartItemId) {
        cartService.deleteCartItem(user, cartItemId);
        return ResponseEntity.ok().build();
    }
}
