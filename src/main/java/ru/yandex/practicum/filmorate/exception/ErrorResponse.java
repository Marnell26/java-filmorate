package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final String errorDescription;
    private final String status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, String> errors = new HashMap<>();

    public ErrorResponse(String errorDescription, String status, Map<String, String> errors) {
        this.errorDescription = errorDescription;
        this.status = status;
        this.errors = errors;
    }

    public ErrorResponse(String errorDescription, String status) {
        this.errorDescription = errorDescription;
        this.status = status;
    }
}
