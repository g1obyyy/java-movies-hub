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

public class PostMovieStrategy extends BaseMovieActionStrategy {
    public PostMovieStrategy(final MoviesStore store, final HttpResponder responder, final HttpRequestParser parser) {
        super(store, responder, parser);
    }

    @Override
    public void execute(HttpExchange exchange, final String idString) throws IOException {
        if (idString != null) {
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
                return;
            }

            Movie movie = store.addMovie(request.title, request.year);
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
