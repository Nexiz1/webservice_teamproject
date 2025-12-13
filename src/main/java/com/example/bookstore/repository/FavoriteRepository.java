package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Favorite;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserAndDeletedFalse(User user);

    Optional<Favorite> findByIdAndDeletedFalse(Long id);

    Optional<Favorite> findByUserAndBookAndDeletedFalse(User user, Book book);

    boolean existsByUserAndBookAndDeletedFalse(User user, Book book);
}
