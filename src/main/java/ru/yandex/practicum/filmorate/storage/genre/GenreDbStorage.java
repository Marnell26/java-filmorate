package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public class GenreDbStorage implements GenreStorage {

    @Override
    public List<Genre> getGenres() {
        return List.of();
    }

    @Override
    public Genre getGenre(int id) {
        return null;
    }
}
