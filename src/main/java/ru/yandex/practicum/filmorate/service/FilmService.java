package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Film film, User user) {
        film.setLikeByUserId(user.getId());
    }

    public void removeLike(Film film, User user) {
        film.removeLikeByUserId(user.getId());
    }

    public List<Film> getPopularFilms() {
        return filmStorage.getFilms()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikeByUserId().size(),
                        film1.getLikeByUserId().size()))
                .limit(10)
                .collect(Collectors.toList());
    }

}
