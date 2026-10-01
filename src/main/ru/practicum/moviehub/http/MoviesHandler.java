package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.strategy.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public final class MoviesHandler implements HttpHandler {
    private final Map<String, MovieActionStrategy> strategies;
    private final HttpResponder responder;
    private final HttpRequestParser parser;

    public MoviesHandler(final MoviesStore moviesStore, final Gson gson) {
        Objects.requireNonNull(moviesStore, "Библиотека фильмов не может быть Null");
        Objects.requireNonNull(gson,"Объект типа Gson не может быть Null");

        responder = HttpResponder.from(gson);
        this.parser = new HttpRequestParser(gson);

        strategies = Map.of(
                "GET", new GetMovieStrategy(moviesStore, responder, parser),
                "POST", new PostMovieStrategy(moviesStore, responder, parser),
                "DELETE", new DeleteMovieStrategy(moviesStore, responder, parser)
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();
        final String path = getNormalizePath(exchange);

        try {
            if (!path.startsWith("/movies")) {
                responder.sendError(exchange, 404, "Not Found",
                        List.of("Некорректный эндпоинт. Используйте /movies или /movies/{id}"));
                return;
            }

            String idString = parser.extractIdString(path, BaseMovieActionStrategy.RESOURCE);

            MovieActionStrategy strategy = strategies.get(method.toUpperCase());
            if (strategy != null) {
                strategy.execute(exchange, idString);
            } else {
                responder.sendError(exchange, 405, "Method Not Allowed",
                        List.of("Метод " + method + " не поддерживается"));
            }
        } catch (Exception e) {
            responder.sendError(exchange, 500, "Internal Server Error",
                    List.of("Произошла непредвиденная ошибка на сервере"));
        }

    }

    private String normalizePath(final String path) {
        if (path.endsWith("/") && path.length() > 1) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String getNormalizePath(HttpExchange exchange) {
        final String path = exchange.getRequestURI().getPath();
        return normalizePath(path);
    }
}
