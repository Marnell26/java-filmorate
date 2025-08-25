package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .login("user1")
                .email("user1@mail.ru")
                .name("Ivan Ivanov")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
    }

    @Test
    public void createUserTest() {
        userController.createUser(user);
        assertEquals(1, userController.getUsers().getFirst().getId());
    }

    @Test
    public void updateUserTest() {
        userController.createUser(user);
        User updatedUser = user;
        updatedUser.setEmail("user1@yandex.ru");
        userController.updateUser(updatedUser);
        assertEquals(updatedUser, userController.getUsers().getFirst());
    }

    @Test
    public void createUserWithSpaceInLogin() {
        User user2 = user;
        user2.setLogin("user 2");
        Exception e = assertThrows(ValidationException.class, () ->
                userController.createUser(user2));
        assertEquals("Логин не должен содержать пробелы", e.getMessage());
    }

}