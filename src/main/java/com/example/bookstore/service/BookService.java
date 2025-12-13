package com.example.bookstore.service;

import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.book.*;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    // Admin: Create book
    @Transactional
    public Long createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.DUPLICATE_ISBN);
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .summary(request.getSummary())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publicationDate(request.getPublicationDate())
                .build();

        return bookRepository.save(book).getId();
    }

    // Public: Get single book
    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
        return BookResponse.from(book);
    }

    // Admin: Get all books (list)
    public List<BookListResponse> getAllBooks() {
        return bookRepository.findAllByDeletedFalse().stream()
                .map(BookListResponse::from)
                .toList();
    }

    // Public: Get books with pagination and search
    public PageResponse<BookResponse> getBooks(String keyword, String author, String publisher, Pageable pageable) {
        Page<Book> books;

        if (keyword != null && !keyword.isEmpty()) {
            books = bookRepository.searchBooks(keyword, pageable);
        } else if (author != null && !author.isEmpty()) {
            books = bookRepository.findByAuthorContaining(author, pageable);
        } else if (publisher != null && !publisher.isEmpty()) {
            books = bookRepository.findByPublisherContaining(publisher, pageable);
        } else {
            books = bookRepository.findByDeletedFalse(pageable);
        }

        return PageResponse.of(books, books.getContent().stream()
                .map(BookResponse::from)
                .toList());
    }

    // Admin: Update book
    @Transactional
    public LocalDateTime updateBook(Long bookId, BookRequest request) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        // Check ISBN duplication (if changed)
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.DUPLICATE_ISBN);
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setSummary(request.getSummary());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublicationDate(request.getPublicationDate());

        bookRepository.save(book);
        return LocalDateTime.now();
    }

    // Admin: Delete book (soft delete)
    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
        book.setDeleted(true);
        bookRepository.save(book);
    }

    // Public: Get book rating
    public BookRatingResponse getBookRating(Long bookId) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Double averageRating = reviewRepository.getAverageRatingByBookId(bookId);
        Long reviewCount = reviewRepository.countByBookId(bookId);

        return BookRatingResponse.builder()
                .bookId(bookId)
                .averageRating(averageRating != null ? Math.round(averageRating * 10) / 10.0 : 0.0)
                .reviewCount(reviewCount)
                .build();
    }
}
