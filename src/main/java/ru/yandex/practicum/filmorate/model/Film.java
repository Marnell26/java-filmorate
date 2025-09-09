package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.OldReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Описание не должно пустым и превышать 200 символов")
    private String description;

    @OldReleaseDate(message = "Дата релиза обязательна и должна быть не раньше 28.12.1895")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    private final Set<Integer> likeByUserId = new HashSet<>();

    public void setLike(int id) {
        likeByUserId.add(id);
    }

    public void removeLike(int id) {
        likeByUserId.remove(id);
    }

}
