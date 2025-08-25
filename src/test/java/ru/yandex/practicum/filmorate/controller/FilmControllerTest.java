package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2011, 1, 10))
                .duration(120)
                .build();
    }

    @Test
    public void createFilmTest() {
        filmController.createFilm(film);
        assertEquals(1, filmController.getFilms().getFirst().getId());
    }

    @Test
    public void updateFilmTest() {
        filmController.createFilm(film);
        Film updatedFilm = film;
        updatedFilm.setDescription("Дополненное описание фильма");
        filmController.updateFilm(updatedFilm);
        assertEquals(updatedFilm, filmController.getFilms().getFirst());
    }

    @Test
    public void EmptyRequestBodyTest() {

    }

    @Test
    public void OldReleaseDateValidation() {

    }


}
