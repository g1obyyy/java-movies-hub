package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpRequestParser {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Gson gson;

    public HttpRequestParser(final Gson gson) {
        this.gson = gson;
    }

    public String extractIdString(final String path, final String resourceName) {
        final String[] tokens = path.split("/");
        if (tokens.length == 3 && tokens[1].equals(resourceName)) {
            return tokens[2];
        }
        return null;
    }

    public boolean isApplicationJson(HttpExchange exchange) {
        List<String> headers = exchange.getRequestHeaders().get("Content-Type");
        if (headers == null) {
            return false;
        }
        return headers.stream()
                .anyMatch(header -> header.toLowerCase().contains("application/json"));
    }

    public <T> T readJsonBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        String body;
        try (InputStream is = exchange.getRequestBody()) {
            body = new String(is.readAllBytes(), DEFAULT_CHARSET);
        }

        if (body.isBlank()) {
            return null;
        }
        return gson.fromJson(body, clazz);
    }
}
