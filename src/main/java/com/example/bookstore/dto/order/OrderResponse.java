package com.example.bookstore.dto.order;

import com.example.bookstore.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private LocalDateTime createdAt;
    private Integer totalAmount;
    private String status;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .build();
    }
}
