package com.example.bookstore.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FirebaseLoginRequest {
    @NotBlank(message = "ID 토큰은 필수입니다.")
    private String idToken;
}