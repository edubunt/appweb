package com.application.appweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp,
        String path,
        int statusCode
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                true,
                message,
                data,
                LocalDateTime.now(),
                null,
                200
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return new ApiResponse<>(
                false,
                message,
                null,
                LocalDateTime.now(),
                null,
                statusCode
        );
    }
}
