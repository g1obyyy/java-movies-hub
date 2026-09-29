package ru.practicum.moviehub.strategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.store.MoviesStoreRAM;

import java.io.IOException;
import java.net.http.HttpClient;

public class BaseApiTest {
    public static final int PORT = 8081;

    protected static MoviesServer server;
    protected static MoviesStore store;
    protected static HttpClient client;
    protected static Gson gson;

    @BeforeAll
    static void setUp() throws IOException {
        store = MoviesStoreRAM.create();

        server = MoviesServer.create(store, PORT);
        server.start();

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @AfterAll
    static void tearDownBase() {
        server.stop();
    }

    @AfterEach
    void clearStore() {
        store.clear();
    }
}
