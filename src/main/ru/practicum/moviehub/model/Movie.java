package ru.practicum.moviehub.model;

import java.util.Objects;

public class Movie {
    private final int id;
    private final String title;
    private final int year;

    private Movie(int id, final String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public static Movie from(int id, final String title, int year) {
        Objects.requireNonNull(title);
        return new Movie(id, title, year);
    }

    public int getId() {
        return id;
    }

    public final String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Movie other = (Movie) o;
        return Objects.equals(id, other.id) &&
                Objects.equals(title, other.title) &&
                Objects.equals(year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year);
    }
}