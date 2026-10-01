package ru.practicum.moviehub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.strategy.BaseApiTest;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@DisplayName("Тестирование общих case'ов handler'а")
public class MoviesApiTest extends BaseApiTest {
    @Test
    @DisplayName("Успешный ответ должен содержать Content-Type: application/json; charset=UTF-8")
    public void responseHasCorrectContentType() throws Exception {
        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        String contentType = response.headers()
                .firstValue("Content-Type")
                .orElse("");
        Assertions.assertEquals("application/json; charset=utf-8", contentType.toLowerCase());
    }

    @Test
    @DisplayName("При неподдерживаемом HTTP-методе возвращается 405 Method Not Allowed")
    public void unsupportedMethodReturns405() throws Exception {
        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(405, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Method Not Allowed", error.error());
    }
}