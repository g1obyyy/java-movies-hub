package ru.practicum.moviehub.strategy;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.exception.ValidationException;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PostMovieStrategy implements MovieActionStrategy {
    public static final String RESOURCE = "movies";

    private final MoviesStore moviesStore;
    private final HttpResponder responder;

    public PostMovieStrategy(final MoviesStore moviesStore, final HttpResponder responder) {
        this.moviesStore = Objects.requireNonNull(moviesStore,"Библиотека фильмов не может быть Null");
        this.responder = Objects.requireNonNull(responder, "Класс Responder должен быть инициализирован");
    }

    @Override
    public void execute(HttpExchange exchange, final String path) throws IOException {
        if (!path.equals("/" + RESOURCE)) {
            responder.sendError(exchange, 404, "Not Found",
                    List.of("POST запросы принимаются только на /" + RESOURCE));
            return;
        }

        if (!responder.isApplicationJson(exchange)) {
            responder.sendError(exchange, 415, "Unsupported Media Type",
                    List.of("Ожидается для Content-Type: " + HttpResponder.CT_JSON));
            return;
        }

        try {
            final String body = responder.readBody(exchange);
            MovieRequest request = responder.getGson().fromJson(body, MovieRequest.class);

            if (request == null) {
                responder.sendError(exchange, 400, "Bad Request", List.of("Тело запроса пустое"));
                return;
            }

            if (request.year == null) {
                responder.sendError(exchange, 422, "Validation Error",
                        List.of("Поле 'year' обязательно к заполнению"));
            }

            Movie movie = moviesStore.addMovie(request.title, request.year);
            responder.sendJson(exchange, 201, responder.getGson().toJson(movie));
        } catch (JsonSyntaxException e) {
            responder.sendError(exchange, 400, "Bad Request", List.of("Некорректный JSON"));
        } catch (ValidationException e) {
            responder.sendError(exchange, 422, "Validation Error", e.getDetails());
        } catch (IllegalStateException e) {
            responder.sendError(exchange, 409, "Conflict", List.of(e.getMessage()));
        }
    }

    private static class MovieRequest {
        String title;
        Integer year;
    }
}
