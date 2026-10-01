package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

public final class MoviesServer {
    private final HttpServer server;
    private final Gson gson;

    private MoviesServer(final MoviesStore moviesStore, int port) throws IOException {
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/movies", new MoviesHandler(moviesStore, gson));
    }

    public static MoviesServer create(final MoviesStore moviesStore, int port) throws IOException {
        Objects.requireNonNull(moviesStore,"Библиотека фильмов не может быть Null");
        return new MoviesServer(moviesStore, port);
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public Gson getGson() {
        return gson;
    }
}