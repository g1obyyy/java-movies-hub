package ru.practicum.moviehub.strategy;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpRequestParser;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GetMovieStrategy extends BaseMovieActionStrategy {
    public GetMovieStrategy(final MoviesStore store, final HttpResponder responder, final HttpRequestParser parser) {
        super(store, responder, parser);
    }

    @Override
    public void execute(HttpExchange exchange, final String idString) throws IOException {
        final String query = exchange.getRequestURI().getQuery();

        if (idString == null) {
            if (query != null && query.startsWith("year=")) {
                processGetByYear(exchange, query);
            } else {
                processGetAll(exchange);
            }
        } else {
            processGetById(exchange, idString);
        }
    }

    private void processGetAll(HttpExchange exchange) throws IOException {
        responder.sendJson(exchange, 200, store.getAllMoviesList());
    }

    private void processGetByYear(HttpExchange exchange, final String query) throws IOException {
        try {
            final String[] parts = query.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Значение года не верно указано");
            }
            int year = Integer.parseInt(parts[1]);

            List<Movie> filtered = store.getListByYear(year);
            responder.sendJson(exchange, 200, filtered);
        } catch (IllegalArgumentException e) {
            responder.sendError(exchange, 400, "Bad Request",
                    List.of("Некорректный параметр запроса year. Ожидается число"));
        }
    }

    private void processGetById(HttpExchange exchange, final String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Optional<Movie> optionalMovie = store.getMovieById(id);
            if (optionalMovie.isPresent()) {
                responder.sendJson(exchange, 200, optionalMovie.get());
            } else {
                responder.sendError(exchange, 404, "Not Found",
                        List.of("Фильм не найден"));
            }
        } catch (NumberFormatException e) {
            responder.sendError(exchange, 400, "Bad Request",
                    List.of("ID должен быть целым числом"));
        }
    }
}
