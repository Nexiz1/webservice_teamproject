package com.example.bookstore.security;

import com.example.bookstore.security.CustomOAuth2User;

import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2 User Attributes: {}", oAuth2User.getAttributes());

        // Google에서 받은 정보 추출
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");
        String provider = "google";

        // DB에서 사용자 찾기 또는 생성
        User user = saveOrUpdate(email, name, provider, providerId);

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(String email, String name, String provider, String providerId) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            // 기존 사용자 업데이트
            user = userOptional.get();
            user.setName(name);
            user.setProvider(provider);
            user.setProviderId(providerId);
            log.info("Existing user updated: {}", email);
        } else {
            // 새 사용자 생성
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setRole(User.Role.ROLE_USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            // OAuth 사용자는 비밀번호 불필요 (null 또는 랜덤값)
            log.info("New user created: {}", email);
        }

        return userRepository.save(user);
    }
}
