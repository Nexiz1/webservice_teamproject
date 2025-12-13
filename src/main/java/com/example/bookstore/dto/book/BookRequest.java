package com.example.bookstore.dto.book;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자 이하여야 합니다")
    private String title;

    @NotBlank(message = "저자는 필수입니다")
    @Size(max = 255, message = "저자는 255자 이하여야 합니다")
    private String author;

    @NotBlank(message = "출판사는 필수입니다")
    @Size(max = 255, message = "출판사는 255자 이하여야 합니다")
    private String publisher;

    private String summary;

    @NotBlank(message = "ISBN은 필수입니다")
    @Size(max = 20, message = "ISBN은 20자 이하여야 합니다")
    private String isbn;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private Integer price;

    private LocalDate publicationDate;
}
