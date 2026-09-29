package ru.practicum.moviehub.strategy;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.HttpResponder;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetMovieStrategy implements MovieActionStrategy {
    private static final String RESOURCE = "movies";

    private final MoviesStore moviesStore;
    private final HttpResponder responder;

    public GetMovieStrategy(final MoviesStore moviesStore, final HttpResponder responder) {
        this.moviesStore = Objects.requireNonNull(moviesStore,"Библиотека фильмов не может быть Null");
        this.responder = Objects.requireNonNull(responder, "Класс Responder должен быть инициализирован");
    }

    @Override
    public void execute(HttpExchange exchange, final String path) throws IOException {
        final String query = exchange.getRequestURI().getQuery();

        if (path.equals("/" + RESOURCE)) {
            if (query != null && query.startsWith("year=")) {
                processGetByYear(exchange, query);
            } else {
                processGetAll(exchange);
            }
            return;
        }

        final String idString = responder.extractIdString(path, RESOURCE);
        if (idString != null) {
            processGetById(exchange, idString);
        } else {
            responder.sendError(exchange, 404, "Not Found",
                    List.of("Некорректный эндпоинт. Используйте /" + RESOURCE + " или /" + RESOURCE + "/{id}"));        }
    }

    private void processGetAll(HttpExchange exchange) throws IOException {
        responder.sendJson(exchange, 200,
                responder.getGson().toJson(moviesStore.getAllMoviesList()));
    }

    private void processGetByYear(HttpExchange exchange, final String query) throws IOException {
        try {
            final String[] parts = query.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Значение года не верно указано");
            }
            int year = Integer.parseInt(parts[1]);

            List<Movie> filtered = moviesStore.getListByYear(year);
            responder.sendJson(exchange, 200, responder.getGson().toJson(filtered));
        } catch (IllegalArgumentException e) {
            responder.sendError(exchange, 400, "Bad Request",
                    List.of("Некорректный параметр запроса year. Ожидается число"));
        }
    }

    private void processGetById(HttpExchange exchange, final String idString) throws IOException {
        try {
            int id = Integer.parseInt(idString);
            Optional<Movie> optionalMovie = moviesStore.getMovieById(id);
            if (optionalMovie.isPresent()) {
                responder.sendJson(exchange, 200, responder.getGson().toJson(optionalMovie.get()));
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
