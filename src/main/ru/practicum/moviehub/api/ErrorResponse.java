package ru.practicum.moviehub.api;

import java.util.List;
import java.util.Objects;

public record ErrorResponse(String error, List<String> details) {
    public ErrorResponse {
        Objects.requireNonNull(error);
        Objects.requireNonNull(details);
    }
}