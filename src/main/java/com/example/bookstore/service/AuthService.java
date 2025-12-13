package com.example.bookstore.service;

import com.example.bookstore.dto.auth.*;
import com.example.bookstore.entity.RefreshToken;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.RefreshTokenRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public Long signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.ROLE_USER)
                .build();

        return userRepository.save(user).getId();
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            // Revoke all previous refresh tokens
            refreshTokenRepository.revokeAllByUser(user);

            // Save new refresh token
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plus(jwtTokenProvider.getRefreshTokenValidity(), ChronoUnit.MILLIS))
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(LoginResponse.UserInfo.builder()
                            .id(user.getId())
                            .role(user.getRole().name())
                            .tokenType("user_token")
                            .build())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (refreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Save new refresh token
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plus(jwtTokenProvider.getRefreshTokenValidity(), ChronoUnit.MILLIS))
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .role(user.getRole().name())
                        .tokenType("user_token")
                        .build())
                .build();
    }

    public void logout(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
