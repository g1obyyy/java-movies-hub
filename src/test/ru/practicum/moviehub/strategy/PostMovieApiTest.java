package ru.practicum.moviehub.strategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.model.Movie;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@DisplayName("Тестирование API POST")
public class PostMovieApiTest extends BaseApiTest {
    private static class MovieRequest {
        String title;
        Integer year;


        public MovieRequest(String title, Integer year) {
            this.title = title;
            this.year = year;
        }
    }

    @Test
    @DisplayName("Успешное добавление фильма")
    public void postMovieWithValidData() throws Exception {
        MovieRequest movieRequest = new MovieRequest("Король лев", 2000);
        String jsonString = gson.toJson(movieRequest);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Movie movie = gson.fromJson(response.body(), Movie.class);
        Assertions.assertEquals("Король лев", movie.getTitle());
        Assertions.assertEquals(2000, movie.getYear());
        Assertions.assertTrue(movie.getId() > 0);
    }

    @Test
    @DisplayName("Ошибка 422 при отсутствии названия фильма (null)")
    public void postMovieWithNullTitle() throws Exception {
        MovieRequest movieRequest = new MovieRequest(null, 2000);
        String jsonString = gson.toJson(movieRequest);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(422, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Validation Error", error.error());
        boolean hasNullTitleError = error.details().stream()
                        .anyMatch(el -> el.contains("Название не должно быть пустым"));
        Assertions.assertTrue(hasNullTitleError);
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 422 при превышении максимальной длины названия")
    public void postMovieWithLongLength() throws Exception {
        final String title = "........................................"
                + "........................................" +
                "........................";
        MovieRequest movieRequest = new MovieRequest(title, 2000);
        String jsonString = gson.toJson(movieRequest);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(422, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Validation Error", error.error());
        boolean hasTitleLengthError = error.details().stream()
                .anyMatch(el -> el.contains("длина не должна превышать 100 символов"));
        Assertions.assertTrue(hasTitleLengthError);
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 422 при некорректном годе выпуска")
    public void postMovieWithIncorrectYear() throws Exception {
        MovieRequest movieRequest = new MovieRequest("Король лев", 3000);
        String jsonString = gson.toJson(movieRequest);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(422, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Validation Error", error.error());
        boolean hasYearError = error.details().stream()
                .anyMatch(el -> el.contains("Год должен быть между"));
        Assertions.assertTrue(hasYearError);
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 415 при неверном Content-Type")
    public void postMovieWithIncorrectMediaType() throws Exception {
        MovieRequest movieRequest = new MovieRequest("Король лев", 3000);
        String jsonString = gson.toJson(movieRequest);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "xml")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(415, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Unsupported Media Type", error.error());
        boolean hasMediaTypeError = error.details().stream()
                .anyMatch(el -> el.contains("Ожидается для Content-Type:"));
        Assertions.assertTrue(hasMediaTypeError);
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 400 при передаче невалидного JSON")
    public void postMovieWithIncorrectJson() throws Exception {
        String brokenJson = "asd: e";

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(brokenJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(422, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Validation Error", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("Некорректный JSON", error.details().getFirst());
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 409 при попытке добавить уже существующий фильм")
    public void postMovieWithDuplicate() throws Exception {
        store.addMovie("Король лев", 2000);

        MovieRequest movieRequest = new MovieRequest("Король лев", 2000);
        String jsonString = gson.toJson(movieRequest);

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(2, store.size());
    }

    @Test
    @DisplayName("Ошибка 400 при отправке запроса с пустым телом")
    public void postMovieWithEmptyBody() throws Exception {
        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(422, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Validation Error", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("Тело запроса пустое", error.details().getFirst());
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 404 при некорректном формате URI")
    public void postMovieWrongUri() throws Exception {
        URI wrongUri = URI.create(URI_FULL + "/" + "131");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(wrongUri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Not Found", error.error());
        Assertions.assertEquals(1, error.details().size());
        Assertions.assertEquals("POST запросы принимаются только на /" + PostMovieStrategy.RESOURCE,
                error.details().getFirst());
        Assertions.assertEquals(0, store.size());
    }

    @Test
    @DisplayName("Ошибка 422 при отсутствии поля year в запросе")
    public void postMovieWithoutYear() throws Exception {
        String jsonString = "{\"title\": \"Король лев\"}";

        URI uri = URI.create(URI_FULL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", HttpResponder.CT_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(422, response.statusCode());
        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
        Assertions.assertEquals("Validation Error", error.error());
        boolean hasYearError = error.details().stream()
                .anyMatch(el -> el.contains("Поле 'year' обязательно"));
        Assertions.assertTrue(hasYearError);
        Assertions.assertEquals(0, store.size());
    }
}
