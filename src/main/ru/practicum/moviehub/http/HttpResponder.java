package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.api.ErrorResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public final class HttpResponder {
    public static final String CT_JSON = "application/json; charset=UTF-8";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Gson gson;

    private HttpResponder(final Gson gson) {
        this.gson = gson;
    }

    public static HttpResponder from(final Gson gson) {
        Objects.requireNonNull(gson, "Объект типа Gson не может быть Null");
        return new HttpResponder(gson);
    }

    public void sendJson(HttpExchange exchange, int status, Object data) throws IOException {
        String jsonString = gson.toJson(data);

        byte[] responseBody = jsonString.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", CT_JSON);
        exchange.sendResponseHeaders(status, responseBody.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody);
        }
    }

    public void sendNoContent(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    public void sendError(HttpExchange exchange, int status, final String error, final List<String> details) throws  IOException {
        final ErrorResponse errorResponse = new ErrorResponse(error, details);
        sendJson(exchange, status, errorResponse);
    }
}