package com.example.bookstore.controller;

import com.example.bookstore.dto.favorite.FavoriteRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Favorite;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.FavoriteRepository;
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
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        favoriteRepository.deleteAll();
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
    @DisplayName("찜 등록 성공")
    void addFavorite_Success() throws Exception {
        FavoriteRequest request = FavoriteRequest.builder()
                .bookId(testBook.getId())
                .build();

        mockMvc.perform(post("/api/favorites")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.favoriteId").exists());
    }

    @Test
    @DisplayName("찜 등록 실패 - 중복")
    void addFavorite_Duplicate() throws Exception {
        // First favorite
        Favorite existing = Favorite.builder()
                .user(testUser)
                .book(testBook)
                .build();
        favoriteRepository.save(existing);

        FavoriteRequest request = FavoriteRequest.builder()
                .bookId(testBook.getId())
                .build();

        mockMvc.perform(post("/api/favorites")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_FAVORITE"));
    }

    @Test
    @DisplayName("찜 목록 조회 성공")
    void getFavorites_Success() throws Exception {
        Favorite favorite = Favorite.builder()
                .user(testUser)
                .book(testBook)
                .build();
        favoriteRepository.save(favorite);

        mockMvc.perform(get("/api/favorites")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload").isArray());
    }

    @Test
    @DisplayName("찜 삭제 성공")
    void deleteFavorite_Success() throws Exception {
        Favorite favorite = Favorite.builder()
                .user(testUser)
                .book(testBook)
                .build();
        favoriteRepository.save(favorite);

        mockMvc.perform(delete("/api/favorites/" + favorite.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("찜이 삭제되었습니다"));
    }
}
