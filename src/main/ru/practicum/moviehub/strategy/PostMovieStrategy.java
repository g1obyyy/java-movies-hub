package ru.practicum.moviehub.strategy;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.exception.ValidationException;
import ru.practicum.moviehub.http.HttpRequestParser;
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
    private final HttpRequestParser parser;

    public PostMovieStrategy(final MoviesStore moviesStore, final HttpResponder responder, final HttpRequestParser parser) {
        this.moviesStore = Objects.requireNonNull(moviesStore,"Библиотека фильмов не может быть Null");
        this.responder = Objects.requireNonNull(responder, "Класс Responder должен быть инициализирован");
        this.parser = Objects.requireNonNull(parser, "Класс Parser должен быть инициализирован");
    }

    @Override
    public void execute(HttpExchange exchange, final String path) throws IOException {
        if (!path.equals("/" + RESOURCE)) {
            responder.sendError(exchange, 404, "Not Found",
                    List.of("POST запросы принимаются только на /" + RESOURCE));
            return;
        }

        if (!parser.isApplicationJson(exchange)) {
            responder.sendError(exchange, 415, "Unsupported Media Type",
                    List.of("Ожидается для Content-Type: " + HttpResponder.CT_JSON));
            return;
        }

        try {
            MovieRequest request = parser.readJsonBody(exchange, MovieRequest.class);

            if (request == null) {
                responder.sendError(exchange, 422, "Validation Error", List.of("Тело запроса пустое"));
                return;
            }

            if (request.year == null) {
                responder.sendError(exchange, 422, "Validation Error",
                        List.of("Поле 'year' обязательно к заполнению"));
            }

            Movie movie = moviesStore.addMovie(request.title, request.year);
            responder.sendJson(exchange, 201, movie);
        } catch (JsonSyntaxException e) {
            responder.sendError(exchange, 422, "Validation Error", List.of("Некорректный JSON"));
        } catch (ValidationException e) {
            responder.sendError(exchange, 422, "Validation Error", e.getDetails());
        }
    }

    private static class MovieRequest {
        String title;
        Integer year;
    }
}
