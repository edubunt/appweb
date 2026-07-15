package com.application.appweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int statusCode,
        String message,
        List<String> errors,
        String path,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int statusCode, String message, String path) {
        return new ErrorResponse(
                statusCode,
                message,
                null,
                path,
                LocalDateTime.now()
        );
    }

    public static ErrorResponse of(int statusCode, String message, List<String> errors, String path) {
        return new ErrorResponse(
                statusCode,
                message,
                errors,
                path,
                LocalDateTime.now()
        );
    }
}
