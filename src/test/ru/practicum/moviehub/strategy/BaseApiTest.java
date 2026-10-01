package ru.practicum.moviehub.strategy;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.store.MoviesStoreRAM;

import java.io.IOException;
import java.net.http.HttpClient;

public class BaseApiTest {
    protected static MoviesServer server;
    protected static MoviesStore store;
    protected static HttpClient client;
    protected static Gson gson;

    protected static String URI_FULL;

    @BeforeAll
    static void setUp() throws IOException {
        store = MoviesStoreRAM.create();

        server = MoviesServer.create(store, 0);
        server.start();

        int actualPort = server.getPort();
        URI_FULL = "http://localhost:" + actualPort + "/movies";

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        gson = server.getGson();
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
