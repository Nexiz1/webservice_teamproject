package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
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
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private Book testBook;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        bookRepository.deleteAll();

        // Create admin user
        User admin = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .name("관리자")
                .role(User.Role.ROLE_ADMIN)
                .build();
        userRepository.save(admin);
        adminToken = jwtTokenProvider.createAccessToken(admin.getEmail(), admin.getRole().name());

        // Create regular user
        User user = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("user123"))
                .name("사용자")
                .role(User.Role.ROLE_USER)
                .build();
        userRepository.save(user);
        userToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());

        // Create test book
        testBook = Book.builder()
                .title("테스트 도서")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .summary("테스트 요약")
                .isbn("9781234567890")
                .price(30000)
                .publicationDate(LocalDate.of(2023, 1, 1))
                .build();
        bookRepository.save(testBook);
    }

    @Test
    @DisplayName("도서 단건 조회 성공 (공개)")
    void getBook_Success() throws Exception {
        mockMvc.perform(get("/api/public/books/" + testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.title").value("테스트 도서"));
    }

    @Test
    @DisplayName("도서 단건 조회 실패 - 존재하지 않는 도서")
    void getBook_NotFound() throws Exception {
        mockMvc.perform(get("/api/public/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
    }

    @Test
    @DisplayName("도서 목록 조회 (페이지네이션)")
    void getBooks_WithPagination() throws Exception {
        mockMvc.perform(get("/api/public/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content").isArray())
                .andExpect(jsonPath("$.payload.page").value(0))
                .andExpect(jsonPath("$.payload.size").value(10));
    }

    @Test
    @DisplayName("도서 검색")
    void searchBooks() throws Exception {
        mockMvc.perform(get("/api/public/books")
                        .param("keyword", "테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content[0].title").value("테스트 도서"));
    }

    @Test
    @DisplayName("도서 생성 성공 (관리자)")
    void createBook_Success() throws Exception {
        BookRequest request = BookRequest.builder()
                .title("새 도서")
                .author("새 저자")
                .publisher("새 출판사")
                .summary("새 요약")
                .isbn("9789876543210")
                .price(25000)
                .publicationDate(LocalDate.of(2024, 1, 1))
                .build();

        mockMvc.perform(post("/api/admin/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.bookId").exists());
    }

    @Test
    @DisplayName("도서 생성 실패 - 권한 없음 (일반 사용자)")
    void createBook_Forbidden() throws Exception {
        BookRequest request = BookRequest.builder()
                .title("새 도서")
                .author("새 저자")
                .publisher("새 출판사")
                .isbn("9789876543210")
                .price(25000)
                .build();

        mockMvc.perform(post("/api/admin/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("도서 수정 성공 (관리자)")
    void updateBook_Success() throws Exception {
        BookRequest request = BookRequest.builder()
                .title("수정된 도서")
                .author("수정된 저자")
                .publisher("수정된 출판사")
                .summary("수정된 요약")
                .isbn("9781234567890")
                .price(35000)
                .publicationDate(LocalDate.of(2024, 1, 1))
                .build();

        mockMvc.perform(put("/api/admin/books/" + testBook.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("도서 삭제 성공 (관리자)")
    void deleteBook_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/books/" + testBook.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
