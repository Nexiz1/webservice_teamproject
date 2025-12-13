package com.example.bookstore.controller;

import com.example.bookstore.dto.auth.LoginRequest;
import com.example.bookstore.dto.auth.SignUpRequest;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트유저")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(User.Gender.MALE)
                .address("서울시")
                .phoneNumber("010-1234-5678")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("유저를 생성했습니다"))
                .andExpect(jsonPath("$.payload.userId").exists());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_DuplicateEmail() throws Exception {
        // Given: 기존 사용자 생성
        User existingUser = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("기존유저")
                .role(User.Role.ROLE_USER)
                .build();
        userRepository.save(existingUser);

        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트유저")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 (이메일 형식)")
    void signUp_InvalidEmail() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("invalid-email")
                .password("password123")
                .name("테스트유저")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // Given: 사용자 생성
        User user = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("테스트유저")
                .role(User.Role.ROLE_USER)
                .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.payload.accessToken").exists())
                .andExpect(jsonPath("$.payload.refreshToken").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_WrongPassword() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("테스트유저")
                .role(User.Role.ROLE_USER)
                .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
}
