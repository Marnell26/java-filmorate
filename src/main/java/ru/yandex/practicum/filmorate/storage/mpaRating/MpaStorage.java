package ru.yandex.practicum.filmorate.storage.mpaRating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    List<Mpa> getAllMpa();

    Mpa getMpa(int id);
}
