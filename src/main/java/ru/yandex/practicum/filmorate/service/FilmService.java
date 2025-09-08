package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void addLike(Film film, User user) {
        if (filmStorage.getFilm(film.getId()) == null) {
            log.error("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        if (userStorage.getUser(user.getId()) == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        film.setLike(user.getId());
        log.info("Лайк к фильму {} добавлен пользователем {}", film.getName(), user.getLogin());
    }

    public void removeLike(Film film, User user) {
        if (filmStorage.getFilm(film.getId()) == null) {
            log.error("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        if (userStorage.getUser(user.getId()) == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        film.removeLike(user.getId());
        log.info("Лайк к фильму {} удален пользователем {}", film.getName(), user.getLogin());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikeByUserId().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
