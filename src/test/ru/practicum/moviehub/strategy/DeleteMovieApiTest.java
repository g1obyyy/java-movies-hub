package ru.practicum.moviehub.strategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.model.Movie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@DisplayName("Тестирование API DELETE")
public class DeleteMovieApiTest extends BaseApiTest {
    private static final String URI_PART = "http://localhost:8081/movies/";

    @Test
    @DisplayName("Успешное удаление фильма по существующему ID")
    public void deleteMovieValidValue() throws Exception {
        Movie savedMovie = store.addMovie("Король лев", 2000);
        int id = savedMovie.getId();

        URI uri = URI.create(URI_PART + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 404 при удалении несуществующего фильма")
    public void deleteMovieMovieNotFound() throws Exception {
        int fakeId = 21312;
        URI uri = URI.create(URI_PART + fakeId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Not Found", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("Фильм не найден", error.details().getFirst());
    }

    @Test
    @DisplayName("Ошибка 400 при некорректном формате ID (не число)")
    public void deleteMovieWrongId() throws Exception {
        final String wrongId = "abd";
        URI uri = URI.create(URI_PART + wrongId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(400, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Bad Request", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("ID должен быть целым числом", error.details().getFirst());
    }

    @Test
    @DisplayName("Ошибка 404 при некорректном формате URI")
    public void deleteMovieWrongUri() throws Exception {
        URI wrongUri = URI.create(URI_PART);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(wrongUri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Not Found", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("DELETE запросы принимаются только на /" +
                DeleteMovieStrategy.RESOURCE + "/{id}", error.details().getFirst());
    }
}
