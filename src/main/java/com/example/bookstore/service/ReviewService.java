package com.example.bookstore.service;

import com.example.bookstore.dto.review.MyReviewsResponse;
import com.example.bookstore.dto.review.ReviewRequest;
import com.example.bookstore.dto.review.ReviewResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long createReview(User user, Long bookId, ReviewRequest request) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        // Check duplicate review
        if (reviewRepository.existsByUserAndBookAndDeletedFalse(user, book)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REVIEW);
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewRepository.save(review).getId();
    }

    public MyReviewsResponse getMyReviews(User user) {
        List<Review> reviews = reviewRepository.findByUserAndDeletedFalse(user);

        return MyReviewsResponse.builder()
                .userId(user.getId())
                .reviewCount(reviews.size())
                .reviews(reviews.stream()
                        .map(ReviewResponse::from)
                        .toList())
                .build();
    }

    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        return ReviewResponse.from(review);
    }

    public List<ReviewResponse> getBookReviews(Long bookId) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        return reviewRepository.findByBookAndDeletedFalse(book).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @Transactional
    public LocalDateTime updateReview(User user, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // Check ownership
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);
        return LocalDateTime.now();
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // Check ownership or admin
        if (!review.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ROLE_ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        review.setDeleted(true);
        reviewRepository.save(review);
    }
}
