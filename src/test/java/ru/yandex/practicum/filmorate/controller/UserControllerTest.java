package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends ControllersTest {

    @ParameterizedTest
    @DisplayName("Тесты валидации с неправильными данными")
    @MethodSource("stringsForValidationTest")
    public void createUserWithWrongData(String testCase, String text) throws Exception {
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
                .andExpect(jsonPath("$.errors[0].message").value(text));
    }

    private static Stream<Arguments> stringsForValidationTest() {
        return Stream.of(
                Arguments.of("SpaceInLogin", "Логин не должен содержать пробелы"),
                Arguments.of("WrongEmail", "Некорректный формат email"),
                Arguments.of("WrongBirthday", "Некорректная дата рождения"),
                Arguments.of("EmptyEmail", "Email не должен быть пустым"),
                Arguments.of("EmptyLogin", "Логин не должен быть пустым")
        );
    }

    private void addFriends() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user)));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user2)));

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест добавления в друзья")
    public void addFriendsTest() throws Exception {
        addFriends();

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsIds[*]").isNotEmpty());
    }

    @Test
    @DisplayName("Тест удаления из друзей")
    public void removeFriendsTest() throws Exception {
        addFriends();

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Тест получения списка друзей")
    public void getFriendListTest() throws Exception {
        addFriends();

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].friendsIds[0]").isNotEmpty());
    }

    @Test
    @DisplayName("Тест получения списка общих друзей")
    public void getCommonFriendListTest() throws Exception {
        addFriends();

        User user3 = new User(0, "user3@mail.ru", "user3", "Ivan", LocalDate.of(1990, 12, 12));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(user3)));

        mockMvc.perform(put("/users/3/friends/1"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/3/friends/2"))
                .andExpect(status().isOk());


        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("3"));
    }

}