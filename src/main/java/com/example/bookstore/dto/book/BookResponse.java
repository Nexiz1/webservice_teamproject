package com.example.bookstore.dto.book;

import com.example.bookstore.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String summary;
    private String isbn;
    private Integer price;
    private LocalDate publicationDate;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .summary(book.getSummary())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .publicationDate(book.getPublicationDate())
                .build();
    }
}
