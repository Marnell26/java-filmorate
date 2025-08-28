package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest extends ControllersTest {

    @ParameterizedTest
    @DisplayName("Тесты валидации с неправильными данными")
    @ValueSource(strings = {"EmptyName", "EmptyDescription", "MaxSymbols", "EmptyReleaseDate", "NegativeDuration",
            "OldReleaseDate"})
    public void createFilmWithWrongData(String testCase) throws Exception {
        String parameter;
        String text;
        switch (testCase) {
            case "EmptyName" -> {
                parameter = "name";
                text = "Имя не должно быть пустым";
                film2.setName(null);
            }
            case "EmptyDescription" -> {
                parameter = "description";
                text = "Описание не должно пустым и превышать 200 символов";
                film2.setDescription("");
            }
            case "MaxSymbols" -> {
                parameter = "description";
                text = "Описание не должно пустым и превышать 200 символов";
                String symbols = "abcdefghijklmnopqrstuvwxyz";
                int descriptionSize = 201;
                String random = new Random().ints(descriptionSize, 0, symbols.length())
                        .mapToObj(symbols::charAt)
                        .map(Object::toString)
                        .collect(Collectors.joining());
                film2.setDescription(random);
            }
            case "EmptyReleaseDate" -> {
                parameter = "releaseDate";
                text = "Дата релиза обязательна";
                film2.setReleaseDate(null);
            }
            case "NegativeDuration" -> {
                parameter = "duration";
                text = "Продолжительность должна быть положительной";
                film2.setDuration(-1);
            }
            case "OldReleaseDate" -> {
                parameter = "error";
                text = "Дата релиза должна быть не раньше 28.12.1895";
                film2.setReleaseDate(LocalDate.of(1895, 12, 27));
            }
            default -> throw new IllegalStateException("Unexpected value: " + testCase);
        }

        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsBytes(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + parameter).value(text));
    }

}
