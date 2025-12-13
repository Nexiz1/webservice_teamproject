package com.example.bookstore.controller;

import com.example.bookstore.dto.review.ReviewRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReviewRepository;
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
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
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
    @DisplayName("리뷰 작성 성공")
    void createReview_Success() throws Exception {
        ReviewRequest request = ReviewRequest.builder()
                .rating(5)
                .comment("정말 좋은 책입니다!")
                .build();

        mockMvc.perform(post("/api/books/" + testBook.getId() + "/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.reviewId").exists());
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 중복 리뷰")
    void createReview_Duplicate() throws Exception {
        // First review
        Review existingReview = Review.builder()
                .user(testUser)
                .book(testBook)
                .rating(4)
                .comment("첫 번째 리뷰")
                .build();
        reviewRepository.save(existingReview);

        // Try to create another review
        ReviewRequest request = ReviewRequest.builder()
                .rating(5)
                .comment("두 번째 리뷰")
                .build();

        mockMvc.perform(post("/api/books/" + testBook.getId() + "/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_REVIEW"));
    }

    @Test
    @DisplayName("리뷰 단건 조회 성공 (공개)")
    void getReview_Success() throws Exception {
        Review review = Review.builder()
                .user(testUser)
                .book(testBook)
                .rating(4)
                .comment("테스트 리뷰")
                .build();
        reviewRepository.save(review);

        mockMvc.perform(get("/api/reviews/" + review.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.comment").value("테스트 리뷰"));
    }

    @Test
    @DisplayName("내 리뷰 조회 성공")
    void getMyReviews_Success() throws Exception {
        Review review = Review.builder()
                .user(testUser)
                .book(testBook)
                .rating(4)
                .comment("테스트 리뷰")
                .build();
        reviewRepository.save(review);

        mockMvc.perform(get("/api/reviews/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.reviewCount").value(1));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() throws Exception {
        Review review = Review.builder()
                .user(testUser)
                .book(testBook)
                .rating(3)
                .comment("원본 리뷰")
                .build();
        reviewRepository.save(review);

        ReviewRequest request = ReviewRequest.builder()
                .rating(5)
                .comment("수정된 리뷰")
                .build();

        mockMvc.perform(patch("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_Success() throws Exception {
        Review review = Review.builder()
                .user(testUser)
                .book(testBook)
                .rating(3)
                .comment("삭제할 리뷰")
                .build();
        reviewRepository.save(review);

        mockMvc.perform(delete("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰가 삭제되었습니다"));
    }
}
