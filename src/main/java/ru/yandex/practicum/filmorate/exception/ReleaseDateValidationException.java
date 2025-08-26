package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReleaseDateValidationException extends RuntimeException {
    private final LocalDate releaseDate;

    public ReleaseDateValidationException(LocalDate releaseDate, String message) {
        super(message);
        this.releaseDate = releaseDate;
    }

}
