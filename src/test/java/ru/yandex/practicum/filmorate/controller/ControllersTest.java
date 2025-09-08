package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllersTest {

    protected Film film;
    protected Film film2;
    protected User user;
    protected User user2;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2011, 1, 10))
                .duration(120)
                .build();
        film2 = Film.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2011, 1, 10))
                .duration(120)
                .build();
        user = User.builder()
                .login("user")
                .email("user@mail.ru")
                .name("Ivan Ivanov")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
        user2 = User.builder()
                .login("user")
                .email("user@mail.ru")
                .name("Ivan Ivanov")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
    }

    @AfterEach
    public void afterEach() {

    }

    @ParameterizedTest
    @DisplayName("Создание объекта с правильными данными")
    @ValueSource(strings = {"/users", "/films"})
    public void createTest(String path) throws Exception {
        byte[] object;
        String parameter;
        String expectedValue;
        switch (path) {
            case "/users" -> {
                object = objectMapper.writeValueAsBytes(user);
                parameter = "login";
                expectedValue = user.getLogin();
            }
            case "/films" -> {
                object = objectMapper.writeValueAsBytes(film);
                parameter = "name";
                expectedValue = film.getName();
            }
            default -> throw new IllegalStateException("Unexpected value: " + path);
        }
        mockMvc.perform(post(path)
                        .content(object)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + parameter).value(expectedValue));
    }

    @ParameterizedTest
    @DisplayName("Обновление существующего объекта с правильными данными")
    @ValueSource(strings = {"/users", "/films"})
    public void updateTest(String path) throws Exception {
        byte[] object;
        byte[] updatedObject;
        String parameter;
        String expectedValue;
        switch (path) {
            case "/users" -> {
                object = objectMapper.writeValueAsBytes(user);
                parameter = "email";
                user2.setId(1);
                user2.setEmail("user1@yandex.ru");
                expectedValue = user2.getEmail();
                updatedObject = objectMapper.writeValueAsBytes(user2);
            }
            case "/films" -> {
                object = objectMapper.writeValueAsBytes(film);
                parameter = "description";
                film2.setId(1);
                film2.setDescription("Дополненное описание фильма");
                expectedValue = film2.getDescription();
                updatedObject = objectMapper.writeValueAsBytes(film2);
            }
            default -> throw new IllegalStateException("Unexpected value: " + path);
        }

        mockMvc.perform(post(path)
                .content(object)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(path)
                        .content(updatedObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + parameter).value(expectedValue));
    }

    @ParameterizedTest
    @DisplayName("Попытка обновить несуществующий объект")
    @ValueSource(strings = {"/users", "/films"})
    public void updateUnknownObject(String path) throws Exception {
        byte[] updatedObject;
        String errorText;
        switch (path) {
            case "/users" -> {
                user2.setId(9999);
                updatedObject = objectMapper.writeValueAsBytes(user2);
                errorText = "Пользователь не найден";
            }
            case "/films" -> {
                film2.setId(9999);
                updatedObject = objectMapper.writeValueAsBytes(film2);
                errorText = "Фильм не найден";
            }
            default -> throw new IllegalStateException("Unexpected value: " + path);
        }
        mockMvc.perform(put(path)
                        .content(updatedObject)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorDescription").value(errorText));
    }

    @ParameterizedTest
    @DisplayName("Получение списка объектов")
    @ValueSource(strings = {"/users", "/films"})
    public void getRequest(String path) throws Exception {

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @DisplayName("Получение объекта по id")
    @ValueSource(strings = {"/users", "/films"})
    public void getOneObjectRequest(String path) throws Exception {

        createTest(path);

        mockMvc.perform(get(path + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "id").value(1));
    }

}
