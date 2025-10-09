package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    public MpaStorage mpaRatingStorage;

    @Autowired
    public MpaService(MpaStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<Mpa> getAllMpa() {
        return mpaRatingStorage.getAllMpa();
    }

    public Mpa getMpa(int id) {
        return mpaRatingStorage.getMpa(id);
    }
}
