package com.example.bookstore.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRatingResponse {

    private Long bookId;
    private Double averageRating;
    private Long reviewCount;
}
