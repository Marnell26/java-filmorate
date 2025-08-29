package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.OldReleaseDate;

import java.time.LocalDate;

@Data
@Builder
@Slf4j
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

}
