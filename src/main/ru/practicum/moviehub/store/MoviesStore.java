package ru.practicum.moviehub.store;

import ru.practicum.moviehub.exception.DuplicateMovieException;
import ru.practicum.moviehub.model.Movie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class MoviesStore {
    private final Map<Integer, Movie> movies = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private MoviesStore() {}

    public MoviesStore create() {
        return new MoviesStore();
    }

    public boolean isEmpty() {
        return movies.isEmpty();
    }

    public boolean isDuplicate(final String title, int year) {
        Objects.requireNonNull(title);
        return movies.values().stream()
                .anyMatch(movie -> Objects.equals(title, movie.getTitle()) &&
                        Objects.equals(year, movie.getYear()));
    }

    public final Map<Integer, Movie> getMovies() {
        return movies;
    }

    public void addMovie(final String title, int year) {
        Objects.requireNonNull(title);
        if (isDuplicate(title, year)) {
            throw new DuplicateMovieException("Фильм с таким названием и годом уже существует");
        }

        int id = idGenerator.getAndIncrement();
        Movie movie = Movie.from(id, title, year);
        movies.put(id, movie);
    }

    public Optional<Movie> getById(int id) {
        return Optional.ofNullable(movies.get(id));
    }

    public boolean removeMovie(int id) {
        return movies.remove(id) != null;
    }

    public List<Movie> getListByYear(int year) {
        return movies.values().stream()
                .filter(movie -> movie.getYear() == year)
                .collect(Collectors.toList());
    }
}