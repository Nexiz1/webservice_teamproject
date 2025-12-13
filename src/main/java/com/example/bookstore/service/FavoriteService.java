package com.example.bookstore.service;

import com.example.bookstore.dto.favorite.FavoriteRequest;
import com.example.bookstore.dto.favorite.FavoriteResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Favorite;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long addFavorite(User user, FavoriteRequest request) {
        Book book = bookRepository.findByIdAndDeletedFalse(request.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        // Check duplicate
        if (favoriteRepository.existsByUserAndBookAndDeletedFalse(user, book)) {
            throw new BusinessException(ErrorCode.DUPLICATE_FAVORITE);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .book(book)
                .build();

        return favoriteRepository.save(favorite).getId();
    }

    public List<FavoriteResponse> getFavorites(User user) {
        return favoriteRepository.findByUserAndDeletedFalse(user).stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    @Transactional
    public void deleteFavorite(User user, Long favoriteId) {
        Favorite favorite = favoriteRepository.findByIdAndDeletedFalse(favoriteId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAVORITE_NOT_FOUND));

        // Check ownership
        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        favorite.setDeleted(true);
        favoriteRepository.save(favorite);
    }
}
