package ru.practicum.moviehub.strategy;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpRequestParser;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.List;

public class DeleteMovieStrategy extends BaseMovieActionStrategy {
    public DeleteMovieStrategy(final MoviesStore store, final HttpResponder responder, final HttpRequestParser parser) {
        super(store, responder, parser);
    }

    @Override
    public void execute(HttpExchange exchange, final String idString) throws IOException {
        if (idString == null) {
            responder.sendError(exchange, 405, "Method Not Allowed",
                    List.of("Удаление всей коллекции не поддерживается"));
            return;
        }

        try {
            int id = Integer.parseInt(idString);
            if (store.removeMovie(id)) {
                responder.sendNoContent(exchange, 204);
            } else {
                responder.sendNoContent(exchange, 404);
            }
        } catch (NumberFormatException e) {
            responder.sendError(exchange, 400, "Bad Request", List.of("ID должен быть целым числом"));
        }
    }
}
