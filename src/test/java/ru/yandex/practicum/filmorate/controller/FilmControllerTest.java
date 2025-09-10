package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest extends ControllersTest {

    @ParameterizedTest
    @DisplayName("Тесты валидации с неправильными данными")
    @MethodSource("stringsForValidationTest")
    public void createFilmWithWrongData(String testCase, String text) throws Exception {
        switch (testCase) {
            case "EmptyName" -> film2.setName(null);
            case "EmptyDescription" -> film2.setDescription("");
            case "MaxSymbols" -> {
                String symbols = "abcdefghijklmnopqrstuvwxyz";
                int descriptionSize = 201;
                String random = new Random().ints(descriptionSize, 0, symbols.length())
                        .mapToObj(symbols::charAt)
                        .map(Object::toString)
                        .collect(Collectors.joining());
                film2.setDescription(random);
            }
            case "EmptyReleaseDate" -> film2.setReleaseDate(null);
            case "NegativeDuration" -> film2.setDuration(-1);
            case "OldReleaseDate" -> film2.setReleaseDate(LocalDate.of(1895, 12, 27));
            default -> throw new IllegalStateException("Unexpected value: " + testCase);
        }
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsBytes(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value(text));
    }

    private static Stream<Arguments> stringsForValidationTest() {
        return Stream.of(
                Arguments.of("EmptyName", "Имя не должно быть пустым"),
                Arguments.of("EmptyDescription", "Описание не должно пустым и превышать 200 символов"),
                Arguments.of("MaxSymbols", "Описание не должно пустым и превышать 200 символов"),
                Arguments.of("EmptyReleaseDate", "Дата релиза обязательна и должна быть не раньше 28.12.1895"),
                Arguments.of("NegativeDuration", "Продолжительность должна быть положительной"),
                Arguments.of("OldReleaseDate", "Дата релиза обязательна и должна быть не раньше 28.12.1895")
        );
    }

    @Test
    @DisplayName("Тест добавления лайка к фильму")
    public void addLikeTest() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(film)));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user)));

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeByUserId[*]").isNotEmpty());
    }

    @Test
    @DisplayName("Тест удаления лайка к фильму")
    public void removeLikeTest() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(film)));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user)));

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест получения самых популярных фильмов по кол-ву лайков")
    public void getPopularFilmsTest() throws Exception {
        film2.setName("Фильм2");
        user2.setLogin("user2");
        Film film3 = new Film(0, "Фильм3", "Описание фильма", LocalDate.of(2011, 1, 10), 120);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user)));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user2)));

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(film)));
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(film2)));
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(film3)));

        mockMvc.perform(put("/films/1/like/1"));
        mockMvc.perform(put("/films/1/like/2"));
        mockMvc.perform(put("/films/2/like/1"));

        mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likeByUserId[0]").value("1"))
                .andExpect(jsonPath("$[0].likeByUserId[1]").value("2"))
                .andExpect(jsonPath("$[1].likeByUserId[0]").value("1"));
    }

}
