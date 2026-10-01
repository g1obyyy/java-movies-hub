package ru.practicum.moviehub;

import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.store.MoviesStoreRAM;

import java.io.IOException;

public class MovieHubApp {
    public static void main(String[] args) {
        try {
            final MoviesStore moviesStore = MoviesStoreRAM.create();
            final MoviesServer server = MoviesServer.create(moviesStore, 0);

            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();
        } catch (IOException e) {
            System.err.println("Критическая ошибка при запуске сервера: " + e.getMessage());
        }
    }
}