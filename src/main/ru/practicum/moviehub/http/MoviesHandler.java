package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.strategy.DeleteMovieStrategy;
import ru.practicum.moviehub.strategy.GetMovieStrategy;
import ru.practicum.moviehub.strategy.MovieActionStrategy;
import ru.practicum.moviehub.strategy.PostMovieStrategy;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public final class MoviesHandler implements HttpHandler {
    private final Map<String, MovieActionStrategy> strategies;
    private final HttpResponder responder;

    public MoviesHandler(final MoviesStore moviesStore, final Gson gson) {
        Objects.requireNonNull(moviesStore, "Библиотека фильмов не может быть Null");
        Objects.requireNonNull(gson,"Объект типа Gson не может быть Null");

        responder = HttpResponder.from(gson);
        strategies = Map.of(
                "GET", new GetMovieStrategy(moviesStore, responder),
                "POST", new PostMovieStrategy(moviesStore, responder),
                "DELETE", new DeleteMovieStrategy(moviesStore, responder)
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();
        final String path = getNormalizePath(exchange);

        try {
            MovieActionStrategy strategy = strategies.get(method.toUpperCase());
            if (strategy != null) {
                strategy.execute(exchange, path);
            } else {
                responder.sendError(exchange, 405, "Method Not Allowed",
                        List.of("Метод " + method + " не поддерживается"));
            }
        } catch (Exception e) {
            responder.sendError(exchange, 500, "Internal Server Error",
                    List.of(e.getMessage()));
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
