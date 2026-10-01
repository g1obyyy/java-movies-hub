package ru.practicum.moviehub.strategy;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpRequestParser;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DeleteMovieStrategy implements MovieActionStrategy {
    public static final String RESOURCE = "movies";

    private final MoviesStore moviesStore;
    private final HttpResponder responder;
    private final HttpRequestParser parser;

    public DeleteMovieStrategy(final MoviesStore moviesStore, final HttpResponder responder, final HttpRequestParser parser) {
        this.moviesStore = Objects.requireNonNull(moviesStore,"Библиотека фильмов не может быть Null");
        this.responder = Objects.requireNonNull(responder, "Класс Responder должен быть инициализирован");
        this.parser = Objects.requireNonNull(parser, "Класс Parser должен быть инициализирован");
    }

    @Override
    public void execute(HttpExchange exchange, final String path) throws IOException {
        String idString = parser.extractIdString(path, RESOURCE);
        if (idString == null)  {
            responder.sendError(exchange, 404, "Not Found",
                    List.of("DELETE запросы принимаются только на /" + RESOURCE + "/{id}"));
            return;
        }

        try {
            int id = Integer.parseInt(idString);
            if (moviesStore.removeMovie(id)) {
                responder.sendNoContent(exchange, 204);
            } else {
                responder.sendError(exchange, 404, "Not Found", List.of("Фильм не найден"));
            }
        } catch (NumberFormatException e) {
            responder.sendError(exchange, 400, "Bad Request", List.of("ID должен быть целым числом"));
        }
    }
}
