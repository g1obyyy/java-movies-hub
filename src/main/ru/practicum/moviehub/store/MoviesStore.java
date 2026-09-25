package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class MoviesStore {
    private final Map<Integer, Movie> movies = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public static MoviesStore create() {
        return new MoviesStore();
    }

    public final List<Movie> getAllMoviesList() {
        return List.copyOf(movies.values());
    }

    public final Movie addMovie(final String title, int year) {
        Objects.requireNonNull(title);
        if (isDuplicate(title, year)) {
            throw new IllegalStateException("Фильм с таким названием и годом уже существует");
        }

        int id = idGenerator.getAndIncrement();
        final Movie movie = Movie.from(id, title, year);
        movies.put(id, movie);
        return movie;
    }

    public Optional<Movie> getMovieById(int id) {
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

    public boolean isDuplicate(final String title, int year) {
        Objects.requireNonNull(title);
        return movies.values().stream()
                .anyMatch(movie -> Objects.equals(title, movie.getTitle()) &&
                        Objects.equals(year, movie.getYear()));
    }

}