package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.book.BookListResponse;
import com.example.bookstore.dto.book.BookRequest;
import com.example.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Book (Admin)", description = "도서 관리자 API")
@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminBookController {

    private final BookService bookService;

    // 10. POST /api/admin/books - 도서 생성 (관리자)
    @Operation(summary = "도서 생성", description = "새로운 도서를 등록합니다 (관리자 전용)")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBook(@Valid @RequestBody BookRequest request) {
        Long bookId = bookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success("도서가 생성되었습니다", Map.of(
                "bookId", bookId,
                "createdAt", LocalDateTime.now()
        )));
    }

    // 11. GET /api/admin/books - 도서 목록 조회 (관리자)
    @Operation(summary = "도서 목록 조회", description = "전체 도서 목록을 조회합니다 (관리자 전용)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookListResponse>>> getAllBooks() {
        List<BookListResponse> response = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
    }

    // 12. PUT /api/admin/books/{bookId} - 도서 수정 (관리자)
    @Operation(summary = "도서 수정", description = "도서 정보를 수정합니다 (관리자 전용)")
    @PutMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateBook(
            @PathVariable("bookId") Long bookId,  // ← 이름 명시!
            @Valid @RequestBody BookRequest request) {
        LocalDateTime updatedAt = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(ApiResponse.success("수정 완료", Map.of(
                "bookId", bookId,
                "updatedAt", updatedAt
        )));
    }

    // 13. DELETE /api/admin/books/{bookId} - 도서 삭제 (관리자)
    @Operation(summary = "도서 삭제", description = "도서를 삭제합니다 (관리자 전용)")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable("bookId")  Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }
}
