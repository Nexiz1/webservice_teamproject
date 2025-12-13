package com.example.bookstore.service;

import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.order.OrderRequest;
import com.example.bookstore.dto.order.OrderResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long createOrder(User user, OrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;

        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.CREATED)
                .totalAmount(0)
                .build();

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Book book = bookRepository.findByIdAndDeletedFalse(itemRequest.getBookId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

            int itemPrice = book.getPrice() * itemRequest.getQuantity();
            totalAmount += itemPrice;

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(book)
                    .quantity(itemRequest.getQuantity())
                    .price(itemPrice)
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        return orderRepository.save(order).getId();
    }

    public OrderResponse getOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // Check ownership (unless admin)
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ROLE_ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return OrderResponse.from(order);
    }

    public PageResponse<OrderResponse> getMyOrders(User user, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return PageResponse.of(orders, orders.getContent().stream()
                .map(OrderResponse::from)
                .toList());
    }

    @Transactional
    public LocalDateTime updateOrderStatus(User user, Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // Check ownership or admin
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ROLE_ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        order.setStatus(status);
        orderRepository.save(order);

        return LocalDateTime.now();
    }

    // Admin: Get all orders
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return PageResponse.of(orders, orders.getContent().stream()
                .map(OrderResponse::from)
                .toList());
    }

    // Admin: Get orders by status
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
