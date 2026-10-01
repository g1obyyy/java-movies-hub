package ru.practicum.moviehub.strategy;

import ru.practicum.moviehub.http.HttpRequestParser;
import ru.practicum.moviehub.http.HttpResponder;
import ru.practicum.moviehub.store.MoviesStore;

import java.util.Objects;

public abstract class BaseMovieActionStrategy implements MovieActionStrategy {
    public static final String RESOURCE = "movies";

    protected final MoviesStore store;
    protected final HttpResponder responder;
    protected final HttpRequestParser parser;

    protected BaseMovieActionStrategy(final MoviesStore store, final HttpResponder responder, final HttpRequestParser parser) {
        this.store = Objects.requireNonNull(store,"Библиотека фильмов не может быть Null");
        this.responder = Objects.requireNonNull(responder, "Класс Responder должен быть инициализирован");
        this.parser = Objects.requireNonNull(parser, "Класс Parser должен быть инициализирован");
    }
}
