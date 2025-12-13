package com.example.bookstore.service;

import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.user.UserResponse;
import com.example.bookstore.dto.user.UserUpdateRequest;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getMyInfo(User user) {
        return UserResponse.from(user);
    }

    @Transactional
    public LocalDateTime updateMyInfo(User user, UserUpdateRequest request) {
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        userRepository.save(user);
        return LocalDateTime.now();
    }

    // Admin methods
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return PageResponse.of(users, users.getContent().stream()
                .map(UserResponse::from)
                .toList());
    }

    public PageResponse<UserResponse> searchUsers(String name, Pageable pageable) {
        Page<User> users = userRepository.findByNameContaining(name, pageable);
        return PageResponse.of(users, users.getContent().stream()
                .map(UserResponse::from)
                .toList());
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional
    public void updateUserRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
}
