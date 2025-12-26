package com.example.bookstore.controller;

import com.example.bookstore.dto.order.OrderRequest;
import com.example.bookstore.dto.order.OrderStatusRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

        testUser = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("user123"))
                .name("사용자")
                .role(User.Role.ROLE_USER)
                .build();
        userRepository.save(testUser);
        userToken = jwtTokenProvider.createAccessToken(testUser.getEmail(), testUser.getRole().name());

        testBook = Book.builder()
                .title("테스트 도서")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .isbn("9781234567890")
                .price(30000)
                .publicationDate(LocalDate.of(2023, 1, 1))
                .build();
        bookRepository.save(testBook);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .items(List.of(
                        OrderRequest.OrderItemRequest.builder()
                                .bookId(testBook.getId())
                                .quantity(2)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.orderId").exists());
    }

    @Test
    @DisplayName("주문 생성 실패 - 인증 없음")
    void createOrder_Unauthorized() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .items(List.of(
                        OrderRequest.OrderItemRequest.builder()
                                .bookId(testBook.getId())
                                .quantity(2)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isFound());
    }

    @Test
    @DisplayName("내 주문 목록 조회 성공")
    void getMyOrders_Success() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.content").isArray());
    }

    @Test
    @DisplayName("주문 상태 변경 성공")
    void updateOrderStatus_Success() throws Exception {
        // First create an order
        OrderRequest createRequest = OrderRequest.builder()
                .items(List.of(
                        OrderRequest.OrderItemRequest.builder()
                                .bookId(testBook.getId())
                                .quantity(1)
                                .build()
                ))
                .build();

        String result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        // Extract orderId from response
        Long orderId = objectMapper.readTree(result).path("payload").path("orderId").asLong();

        // Update status
        OrderStatusRequest statusRequest = OrderStatusRequest.builder()
                .status(Order.OrderStatus.CANCELLED)
                .build();

        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.status").value("CANCELLED"));
    }
}
