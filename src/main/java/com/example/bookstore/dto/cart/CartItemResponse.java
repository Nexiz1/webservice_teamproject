package com.example.bookstore.dto.cart;

import com.example.bookstore.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private Long bookId;
    private Integer quantity;

    public static CartItemResponse from(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .bookId(cartItem.getBook().getId())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
