package ru.practicum.moviehub.exception;

public class DuplicateMovieException extends RuntimeException {
    public DuplicateMovieException(String message) {
        super(message);
    }
}
