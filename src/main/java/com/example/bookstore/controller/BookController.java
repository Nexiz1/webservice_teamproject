package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.PageResponse;
import com.example.bookstore.dto.book.BookRatingResponse;
import com.example.bookstore.dto.book.BookResponse;
import com.example.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book (Public)", description = "도서 공개 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 7. GET /api/public/books/{bookId} - 도서 단건 조회 (공개)
    @Operation(summary = "도서 단건 조회", description = "도서 ID로 상세 정보를 조회합니다")
    @GetMapping("/public/books/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable("bookId") Long bookId) {
        BookResponse response = bookService.getBook(bookId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 8. GET /api/public/books - 도서 목록 조회 (공개, 페이지네이션/검색)
    @Operation(summary = "도서 목록 조회", description = "도서 목록을 페이지네이션하여 조회합니다. 키워드, 저자, 출판사로 검색 가능합니다")
    @GetMapping("/public/books")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<BookResponse> response = bookService.getBooks(keyword, author, publisher, pageable);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 9. GET /api/books/{bookId}/rating - 도서별 평점 조회 (공개)
    @Operation(summary = "도서 평점 조회", description = "도서의 평균 평점과 리뷰 수를 조회합니다")
    @GetMapping("/books/{bookId}/rating")
    public ResponseEntity<BookRatingResponse> getBookRating(@PathVariable("bookId") Long bookId) {
        BookRatingResponse response = bookService.getBookRating(bookId);
        return ResponseEntity.ok(response);
    }
}
