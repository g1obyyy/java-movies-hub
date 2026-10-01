package ru.practicum.moviehub.strategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.model.Movie;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GetMovieApiTest extends BaseApiTest {
    @Test
    @DisplayName("GET /movies возвращает пустой список, если нет фильмов")
    public void getAllMoviesEmptyList() throws Exception {
        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken());
        Assertions.assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("GET /movies возвращает список с ранее добавленными фильмами")
    public void getAllMoviesValid() throws Exception {
        Movie savedMovie1 = store.addMovie("Король лев", 2000);
        Movie savedMovie2 = store.addMovie("Лев король", 2001);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken());
        Assertions.assertEquals(2, movies.size());

        Movie firstMovie = movies.stream()
                .filter(m -> m.getId() == savedMovie1.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Первый фильм не найден"));

        Movie secondMovie = movies.stream()
                .filter(m -> m.getId() == savedMovie2.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Второй фильм не найден"));

        Assertions.assertEquals("Король лев", firstMovie.getTitle());
        Assertions.assertEquals("Лев король", secondMovie.getTitle());
    }

    @Test
    @DisplayName("GET /movies/{id} возвращает фильм по существующему id")
    public void getMovieByIdValid() throws Exception {
        Movie savedMovie = store.addMovie("Король лев", 2000);
        int id = savedMovie.getId();

        URI uri = URI.create(URI_FULL + "/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Movie foundMovie = gson.fromJson(response.body(), Movie.class);
        Assertions.assertEquals("Король лев", foundMovie.getTitle());
        Assertions.assertEquals(2000, foundMovie.getYear());
        Assertions.assertEquals(id, foundMovie.getId());
    }

    @Test
    @DisplayName("GET /movies/{id} возвращает ошибку 404, если фильм не найден")
    public void getMovieByIdNotFound() throws Exception {
        URI uri = URI.create(URI_FULL + "/" + "999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Not Found", error.error());
        Assertions.assertEquals("Фильм не найден", error.details().getFirst());
    }

    @Test
    @DisplayName("GET /movies/{id} возвращает ошибку 400, если id не число")
    public void getMovieById_notNumber() throws Exception {
        URI uri = URI.create(URI_FULL + "/" + "asd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(400, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Bad Request", error.error());
        Assertions.assertEquals("ID должен быть целым числом", error.details().getFirst());
    }

    @Test
    @DisplayName("GET /movies?year=YYYY возвращает фильмы указанного года")
    public void getMoviesByYear_success() throws Exception {
        store.addMovie("Король лев", 2000);
        store.addMovie("Лев король", 2000);
        store.addMovie("Кроль лев", 2001);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FULL + "?year=2000"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken());

        Assertions.assertEquals(2, movies.size());
        boolean allMatch = movies.stream()
                    .allMatch(m -> m.getYear() == 2000);
        Assertions.assertTrue(allMatch);
    }

    @Test
    @DisplayName("GET /movies?year=YYYY возвращает пустой список, если фильмов с таким годом нет")
    public void getMoviesByYear_emptyList() throws Exception {
        store.addMovie("Король лев", 2000);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FULL + "?year=2001"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        List<Movie> movies = gson.fromJson(response.body(), new ListOfMoviesTypeToken());
        Assertions.assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("GET /movies?year=YYYY возвращает ошибку 400, если параметр year не число")
    public void getMoviesByYear_notNumber() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_FULL + "?year=adx"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(400, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Bad Request", error.error());
        Assertions.assertEquals("Некорректный параметр запроса year. Ожидается число",
                error.details().getFirst());
    }

    @Test
    @DisplayName("Ошибка 400 при некорректном формате URI")
    public void getMovieWrongUri() throws Exception {
        URI wrongUri = URI.create(URI_FULL + "/" + "qdq");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(wrongUri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(400, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Bad Request", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("ID должен быть целым числом",
                error.details().getFirst());
    }
}
