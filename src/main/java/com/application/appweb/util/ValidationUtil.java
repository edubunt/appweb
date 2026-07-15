package com.application.appweb.util;

import com.application.appweb.exception.InvalidInputException;

import java.time.LocalDate;

public class ValidationUtil {

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidInputException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidInputException("Start date must be before end date");
        }
    }

    public static void validateStringNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidInputException(fieldName + " cannot be null");
        }
    }
}
