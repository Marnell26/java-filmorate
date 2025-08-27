package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends ControllersTest {

    @ParameterizedTest
    @DisplayName("Тесты валидации с неправильными данными")
    @ValueSource(strings = {"SpaceInLogin", "WrongEmail", "WrongBirthday", "EmptyEmail", "EmptyLogin"})
    public void createUserWithSpaceInLogin(String testCase) throws Exception {
        String parameter;
        String text;
        switch (testCase) {
            case "SpaceInLogin" -> {
                parameter = "login";
                text = "Логин не должен содержать пробелы";
                user2.setLogin("user 2");
            }
            case "WrongEmail" -> {
                parameter = "email";
                text = "Некорректный формат email";
                user2.setEmail("mail.ru");
            }
            case "WrongBirthday" -> {
                parameter = "birthday";
                text = "Некорректная дата рождения";
                user2.setBirthday(LocalDate.of(2446, 8, 20));
            }
            case "EmptyEmail" -> {
                parameter = "email";
                text = "Email не должен быть пустым";
                user2.setEmail(null);
            }
            case "EmptyLogin" -> {
                parameter = "login";
                text = "Логин не должен быть пустым";
                user2.setLogin(null);
            }
            default -> throw new IllegalStateException("Unexpected value: " + testCase);
        }

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + parameter).value(text));
    }

}