package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends ControllersTest {

    @ParameterizedTest
    @DisplayName("Тесты валидации с неправильными данными")
    @MethodSource("stringsForValidationTest")
    public void createUserWithWrongData(String testCase, String parameter, String text) throws Exception {
        switch (testCase) {
            case "SpaceInLogin" -> user2.setLogin("user 2");
            case "WrongEmail" -> user2.setEmail("mail.ru");
            case "WrongBirthday" -> user2.setBirthday(LocalDate.of(2446, 8, 20));
            case "EmptyEmail" -> user2.setEmail(null);
            case "EmptyLogin" -> user2.setLogin(null);
            default -> throw new IllegalStateException("Unexpected value: " + testCase);
        }
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors." + parameter).value(text));
    }

    private static Stream<Arguments> stringsForValidationTest() {
        return Stream.of(
                Arguments.of("SpaceInLogin", "login", "Логин не должен содержать пробелы"),
                Arguments.of("WrongEmail", "email", "Некорректный формат email"),
                Arguments.of("WrongBirthday", "birthday", "Некорректная дата рождения"),
                Arguments.of("EmptyEmail", "email", "Email не должен быть пустым"),
                Arguments.of("EmptyLogin", "login", "Логин не должен быть пустым")
        );
    }

}