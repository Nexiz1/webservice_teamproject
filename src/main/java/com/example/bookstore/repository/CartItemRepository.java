package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartAndDeletedFalse(Cart cart);

    Optional<CartItem> findByCartAndBookAndDeletedFalse(Cart cart, Book book);

    Optional<CartItem> findByIdAndDeletedFalse(Long id);
}
