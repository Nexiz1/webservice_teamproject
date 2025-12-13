package com.example.bookstore.controller;

import com.example.bookstore.dto.cart.CartItemRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.CartRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
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
    @DisplayName("장바구니 항목 추가 성공")
    void addCartItem_Success() throws Exception {
        CartItemRequest request = CartItemRequest.builder()
                .bookId(testBook.getId())
                .quantity(2)
                .build();

        mockMvc.perform(post("/api/carts/items")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.cartId").exists());
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCartItems_Success() throws Exception {
        // First add an item
        CartItemRequest request = CartItemRequest.builder()
                .bookId(testBook.getId())
                .quantity(2)
                .build();

        mockMvc.perform(post("/api/carts/items")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then retrieve
        mockMvc.perform(get("/api/carts/items")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload").isArray());
    }

    @Test
    @DisplayName("장바구니 수량 수정 성공")
    void updateCartItem_Success() throws Exception {
        // First add an item
        CartItemRequest addRequest = CartItemRequest.builder()
                .bookId(testBook.getId())
                .quantity(2)
                .build();

        mockMvc.perform(post("/api/carts/items")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)));

        // Update quantity
        CartItemRequest updateRequest = CartItemRequest.builder()
                .bookId(testBook.getId())
                .quantity(5)
                .build();

        mockMvc.perform(put("/api/carts/items")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }
}
