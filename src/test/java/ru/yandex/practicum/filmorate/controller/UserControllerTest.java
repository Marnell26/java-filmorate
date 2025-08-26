package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebMvcTest(UserController.class)
class UserControllerTest {

    private UserController userController;
    private User user;

    @Autowired
    private MockMvc mockMvc;

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
        userController.createUser(user2);

    }

}