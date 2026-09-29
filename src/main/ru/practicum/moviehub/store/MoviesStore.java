package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MoviesStore {
    List<Movie> getAllMoviesList();
    Movie addMovie(final String title, int year);
    Optional<Movie> getMovieById(int id);
    boolean removeMovie(int id);
    List<Movie> getListByYear(int year);
    int size();
}
