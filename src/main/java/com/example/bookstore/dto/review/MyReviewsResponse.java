package com.example.bookstore.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewsResponse {

    private Long userId;
    private Integer reviewCount;
    private List<ReviewResponse> reviews;
}
