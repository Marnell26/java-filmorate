package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Slf4j
public class User {
    private int id;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;

    private final Set<Integer> friendsIds;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        if (name == null || name.isBlank()) {
            this.name = login;
        }
        this.birthday = birthday;
        friendsIds = new HashSet<>();
    }

    public void setFriendId(int id) {
        friendsIds.add(id);
    }

    public void removeFriendId(int id) {
        friendsIds.remove(id);
    }
}