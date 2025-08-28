package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.ReleaseDateValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateValidator {
    public static void validateFilm(Film newFilm) throws ReleaseDateValidationException {
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28)))
            throw new ReleaseDateValidationException(newFilm.getReleaseDate(), "Дата релиза должна быть не раньше " +
                    "28.12.1895");
    }
}
