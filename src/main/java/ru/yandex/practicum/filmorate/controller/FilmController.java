package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReleaseDateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidator;

import java.time.LocalDateTime;
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
        } catch (ReleaseDateValidationException e) {
            log.debug(e.getMessage());
            throw e;
        }
    }


    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            String message = "Фильм не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        try {
            ReleaseDateValidator.validateFilm(film);
            films.put(film.getId(), film);
            log.info("Информация о фильме {} обновлена", film.getName());
            return film;
        } catch (ReleaseDateValidationException e) {
            log.debug(e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        errors.put("timestamp", LocalDateTime.now().toString());
        errors.put("status", e.getStatusCode().toString());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReleaseDateValidationException.class)
    public Map<String, String> dateValidationException(ReleaseDateValidationException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now().toString());
        errors.put("status", "400");
        errors.put("Введенная дата релиза", String.valueOf(e.getReleaseDate()));
        errors.put("error", e.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundExceptions(NotFoundException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now().toString());
        errors.put("status", "404");
        errors.put("error", e.getMessage());
        return errors;
    }

    private Integer generateId() {
        return id++;
    }

}
