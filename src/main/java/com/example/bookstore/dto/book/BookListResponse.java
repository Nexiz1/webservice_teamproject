package com.example.bookstore.dto.book;

import com.example.bookstore.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListResponse {

    private Long bookId;
    private String title;
    private String author;
    private String publisher;

    public static BookListResponse from(Book book) {
        return BookListResponse.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .build();
    }
}
