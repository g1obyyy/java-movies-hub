package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.store.MoviesStoreRAM;

import java.util.List;
import java.util.Optional;

public class MoviesStoreMemoryTest {
    private MoviesStore store;

    @BeforeEach
    void setup() {
        store = MoviesStoreRAM.create();
    }

    @Test
    public void getAllMoviesListEmpty() {
        Assertions.assertTrue(store.getAllMoviesList().isEmpty());
    }

    @Test
    public void getAllMoviesListNotEmpty() {
        Movie movie1 = store.addMovie("Король лев", 2000);
        Movie movie2 = store.addMovie("Лев король", 2001);

        Assertions.assertEquals(2, store.getAllMoviesList().size());
    }

    @Test
    public void addMovieWithValidData() {
        Movie movie = store.addMovie("Король лев", 2000);

        Assertions.assertNotNull(movie);
        Assertions.assertEquals("Король лев", movie.getTitle());
        Assertions.assertEquals(2000, movie.getYear());
        Assertions.assertTrue(movie.getId() > 0);
        Assertions.assertEquals(1, store.size());
    }

    @Test
    public void addMovieWithDuplicateData() {
        Movie movie = store.addMovie("Король лев", 2000);

        Assertions.assertThrows(IllegalStateException.class, () -> store.addMovie("Король лев", 2000));
    }

    @Test
    public void getMovieByIdValid() {
        Movie savedMovie = store.addMovie("Король лев", 2000);
        int id = savedMovie.getId();

        Optional<Movie> foundMovie = store.getMovieById(id);

        Assertions.assertTrue(foundMovie.isPresent());
        Assertions.assertEquals("Король лев", foundMovie.get().getTitle());
        Assertions.assertEquals(2000, foundMovie.get().getYear());
    }

    @Test
    public void getMovieByIdFailed() {
        Optional<Movie> optionalMovie = store.getMovieById(1);

        Assertions.assertTrue(optionalMovie.isEmpty());
    }

    @Test
    public void removeMovieValid() {
        Movie savedMovie = store.addMovie("Король лев", 2000);
        int id = savedMovie.getId();

        Assertions.assertTrue(store.removeMovie(id));
        Assertions.assertTrue(store.getMovieById(id).isEmpty());
        Assertions.assertEquals(0, store.size());
    }

    @Test
    public void getListByYearValid() {
        Movie movie1 = store.addMovie("Король лев", 2000);
        Movie movie2 = store.addMovie("Лев Король", 2000);
        Movie movie3 = store.addMovie("Короли лев", 2001);

        List<Movie> movies = store.getListByYear(2000);
        Assertions.assertEquals(2, movies.size());
        Assertions.assertTrue(movies.stream()
                .allMatch(movie -> 2000 == movie.getYear()));
    }

    @Test
    public void getListByYearEmpty() {
        Assertions.assertTrue(store.getListByYear(2001).isEmpty());
    }
}
