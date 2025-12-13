package com.example.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private String message;
    private T payload;

    public static <T> ApiResponse<T> success(String message, T payload) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .payload(payload)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T payload) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .message(message)
                .payload(payload)
                .build();
    }
}
