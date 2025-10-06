package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaRatingStorage;

import java.util.List;

@Service
@Slf4j
public class MpaRatingService {
    public MpaRatingStorage mpaRatingStorage;

    @Autowired
    public MpaRatingService(MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<MpaRating> getMpaRatings() {
        return mpaRatingStorage.getMpaRatings();
    }

    public MpaRating getMpaRating(int id) {
        return mpaRatingStorage.getMpaRating(id);
    }
}
