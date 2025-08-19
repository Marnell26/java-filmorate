package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    Integer id = 0;

    @PostMapping
    public void createUser(@RequestBody User user) {
        users.put(user.getId(), user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        users.put(user.getId(), user);
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<User>(users.values());
    }

    public Integer generateId() {
        return id++;
    }

}
