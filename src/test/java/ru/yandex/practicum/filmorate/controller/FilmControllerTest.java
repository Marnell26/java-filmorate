package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
