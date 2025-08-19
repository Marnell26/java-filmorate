package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    Map<Integer, Film> films = new HashMap<>();
    Integer id = 0;

    @PostMapping
    public void createFilm(@RequestBody Film film) {
        films.put(generateId(), film);
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {
        films.put(film.getId(), film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<Film>(films.values());
    }

    public Integer generateId() {
        return id++;
    }

}
