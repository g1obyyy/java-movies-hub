package ru.practicum.moviehub.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> details;

    public ValidationException(List<String> details) {
        this.details = details;
    }

    public List<String> getDetails() {
        return details;
    }
}
