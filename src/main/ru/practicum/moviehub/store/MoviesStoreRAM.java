package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class MoviesStoreRAM implements MoviesStore {
    private final Map<Integer, Movie> movies = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public static MoviesStoreRAM create() {
        return new MoviesStoreRAM();
    }

    @Override
    public List<Movie> getAllMoviesList() {
        return List.copyOf(movies.values());
    }

    @Override
    public synchronized Movie addMovie(final String title, int year) {
        int currentId = idGenerator.get();
        final Movie movie = Movie.from(currentId, title, year);
        idGenerator.incrementAndGet();
        movies.put(currentId, movie);
        return movie;
    }

    @Override
    public Optional<Movie> getMovieById(int id) {
        return Optional.ofNullable(movies.get(id));
    }

    @Override
    public boolean removeMovie(int id) {
        return movies.remove(id) != null;
    }

    @Override
    public List<Movie> getListByYear(int year) {
        return movies.values().stream()
                .filter(movie -> movie.getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        movies.clear();
        idGenerator.set(1);
    }

    @Override
    public int size() {
        return movies.size();
    }
}