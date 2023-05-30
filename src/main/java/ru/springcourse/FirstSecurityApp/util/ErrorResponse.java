package ru.springcourse.FirstSecurityApp.util;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private LocalDateTime localDateTime;

    public ErrorResponse(String message, LocalDateTime localDateTime) {
        this.message = message;
        this.localDateTime = localDateTime;
    }
}
