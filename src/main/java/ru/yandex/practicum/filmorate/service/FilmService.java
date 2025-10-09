package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService, MpaService mpaService,
            GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film createFilm(Film film) {
        validateMpaAndGenre(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateMpaAndGenre(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmStorage.getFilm(filmId).getId(), userService.getUser(userId).getId());
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmStorage.getFilm(filmId).getId(), userService.getUser(userId).getId());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateMpaAndGenre(Film film) {
        mpaService.getMpa(film.getMpa().getId());
        Set<Integer> allGenresIds = genreService.getAllGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Integer> filmGenresIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        if (!allGenresIds.containsAll(filmGenresIds)) {
            filmGenresIds.removeAll(allGenresIds);
            throw new NotFoundException("В базе нет жанров с id: " + filmGenresIds);
        }
    }

}
