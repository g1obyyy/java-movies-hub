package ru.practicum.moviehub;

import ru.practicum.moviehub.http.MoviesServer;
import java.io.IOException;

public class MovieHubApp {
    public static void main(String[] args) {
        try {
            final MoviesServer server = MoviesServer.create();
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();
        } catch (IOException e) {
            System.err.println("Критическая ошибка при запуске сервера: " + e.getMessage());
        }
    }
}