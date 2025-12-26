package com.example.bookstore.service;

import com.example.bookstore.dto.auth.*;
import com.example.bookstore.entity.RefreshToken;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.RefreshTokenRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.JwtTokenProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
import java.util.UUID;

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

    public LoginResponse firebaseLogin(FirebaseLoginRequest request) {
        String idToken = request.getIdToken();

        try {
            // 1. Firebase ID Token 검증
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName(); // 설정되지 않았을 수 있음

            // 2. 사용자 존재 여부 확인 및 자동 가입 처리
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // 사용자가 없으면 회원가입 진행
                        return userRepository.save(User.builder()
                                .email(email)
                                // OAuth 사용자는 비밀번호가 없지만, DB 제약조건(Not Null)이 있다면 임시값 설정
                                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                .name(name != null ? name : "Firebase User")
                                .role(User.Role.ROLE_USER)
                                .provider("firebase")
                                .providerId(uid)
                                .build());
                    });

            // 3. 기존 사용자가 다른 경로(일반 가입)로 가입되어 있을 경우 provider 정보 업데이트 (선택 사항)
            if (user.getProvider() == null) {
                user.setProvider("firebase");
                user.setProviderId(uid);
                userRepository.save(user);
            }

            // 4. 앱 자체 JWT 토큰 발급 (기존 로그인 로직 재사용)
            String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            // 5. Refresh Token 저장 (기존 로직과 동일)
            refreshTokenRepository.revokeAllByUser(user);
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
                            .tokenType("firebase_token")
                            .build())
                    .build();

        } catch (FirebaseAuthException e) {
            // 토큰 검증 실패 시 예외 처리
            throw new BusinessException(ErrorCode.INVALID_TOKEN); // ErrorCode에 맞게 조정 필요
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
