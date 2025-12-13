package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIdAndDeletedFalse(Long id);

    Page<Book> findByDeletedFalse(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.deleted = false AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.deleted = false AND " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))")
    Page<Book> findByAuthorContaining(@Param("author") String author, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.deleted = false AND " +
            "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%'))")
    Page<Book> findByPublisherContaining(@Param("publisher") String publisher, Pageable pageable);

    boolean existsByIsbn(String isbn);

    List<Book> findAllByDeletedFalse();
}
