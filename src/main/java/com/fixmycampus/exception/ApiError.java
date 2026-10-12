package com.fixmycampus.exception;

import java.time.LocalDateTime;

public record ApiError(int status, String message, LocalDateTime timestamp) {
    public ApiError(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
