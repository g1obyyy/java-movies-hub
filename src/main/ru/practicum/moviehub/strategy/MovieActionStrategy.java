package ru.practicum.moviehub.strategy;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public interface MovieActionStrategy {
    public void execute(HttpExchange exchange, final String path) throws IOException;
}
