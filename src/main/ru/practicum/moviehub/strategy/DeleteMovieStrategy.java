package ru.practicum.moviehub.strategy;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DeleteMovieStrategy implements MovieActionStrategy {
    private static final String RESOURCE = "movies";

    private final MoviesStore moviesStore;
    private final HttpResponder responder;

    public DeleteMovieStrategy(final MoviesStore moviesStore, final HttpResponder responder) {
        this.moviesStore = Objects.requireNonNull(moviesStore,"Библиотека фильмов не может быть Null");
        this.responder = Objects.requireNonNull(responder, "Класс Responder должен быть инициализирован");
    }

    @Override
    public void execute(HttpExchange exchange, final String path) throws IOException {
        String idString = responder.extractIdString(path, RESOURCE);
        if (idString == null)  {
            responder.sendError(exchange, 400, "Bad Request", List.of("Эндпоинт не найден"));
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
