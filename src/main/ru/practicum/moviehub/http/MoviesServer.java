package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.store.MoviesStoreRAM;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class MoviesServer {
    public static final int PORT = 8080;

    private final HttpServer server;

    private MoviesServer() throws IOException {
        MoviesStore moviesStore = MoviesStoreRAM.create();
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/movies", new MoviesHandler(moviesStore, gson));
    }

    public static MoviesServer create() throws IOException {
        return new MoviesServer();
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }
}