package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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

    public void addLike(int id, int userId) {
        getFilm(id).setLike(userService.getUser(userId).getId());
        log.info("Лайк к фильму {} добавлен пользователем {}", getFilm(id).getName(), userId);
    }

    public void removeLike(int id, int userId) {
        getFilm(id).removeLike(userService.getUser(userId).getId());
        log.info("Лайк к фильму {} удален пользователем {}", getFilm(id).getName(), userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikeByUserId().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
