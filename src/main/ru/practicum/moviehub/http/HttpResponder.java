package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.api.ErrorResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final String CT_JSON = "application/json; charset=UTF-8";

    protected final Gson gson;

    protected BaseHttpHandler(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "Объект типа Gson не может быть Null");
    }

    protected void sendJson(HttpExchange exchange, int status, final String json) throws IOException {
        byte[] responseBody = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", CT_JSON);
        exchange.sendResponseHeaders(status, responseBody.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody);
        }
    }

    protected void sendNoContent(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    protected void sendError(HttpExchange exchange, int status, final String error, final List<String> details) throws  IOException{
        final ErrorResponse errorResponse = new ErrorResponse(error, details);
        String jsonError = gson.toJson(errorResponse);
        sendJson(exchange, status, jsonError);
    }

    protected final String extractIdString(final String path, final String resourceName) {
        final String[] tokens = path.split("/");
        if (tokens.length == 3 && tokens[1].equals(resourceName)) {
            return tokens[2];
        }
        return null;
    }
}