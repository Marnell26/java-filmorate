package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        try {
            ReleaseDateValidator.validateFilm(film);
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Фильм {} добавлен", film.getName());
            return film;
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw e;
        }
    }


    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            String message = "Фильм не найден";
            log.error(message);
            throw new ValidationException(message);
        }
        films.put(film.getId(), film);
        log.info("Информация о фильме {} обновлена", film.getName());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<Film>(films.values());
    }

    public Integer generateId() {
        return id++;
    }

}
