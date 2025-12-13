package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndDeletedFalse(Long id);

    List<Review> findByUserAndDeletedFalse(User user);

    Page<Review> findByUserAndDeletedFalse(User user, Pageable pageable);

    List<Review> findByBookAndDeletedFalse(Book book);

    Page<Review> findByBookAndDeletedFalse(Book book, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.deleted = false")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId AND r.deleted = false")
    Long countByBookId(@Param("bookId") Long bookId);

    boolean existsByUserAndBookAndDeletedFalse(User user, Book book);
}
