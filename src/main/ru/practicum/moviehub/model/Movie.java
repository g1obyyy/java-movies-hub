package ru.practicum.moviehub.model;

import java.time.Year;
import java.util.Objects;

public final class Movie {
    private static final int INITIAL_YEAR = 1888;
    private static final int FINAL_YEAR = Year.now().getValue() + 1;
    private static final int MAX_TITLE_LENGTH = 100;

    private final int id;
    private final String title;
    private final int year;

    private Movie(int id, final String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public static Movie from(int id, final String title, int year) {
        if (!isValidTitle(title)) {
            throw new IllegalArgumentException("Название не должно быть пустым, а длина не должна превышать 100 символов");
        }

        if (!isValidYear(year)) {
            int maxYear = Year.now().getValue() + 1;
            throw new IllegalArgumentException("Год должен быть между 1888 и " + maxYear);
        }

        return new Movie(id, title, year);
    }

    private static boolean isValidYear(int year) {
        return year >= INITIAL_YEAR && year < FINAL_YEAR;
    }

    private static boolean isValidTitle(final String title) {
        return title != null && !title.isBlank() && title.length() <= MAX_TITLE_LENGTH;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
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
        return id == other.id &&
                Objects.equals(title, other.title) &&
                Objects.equals(year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year);
    }
}