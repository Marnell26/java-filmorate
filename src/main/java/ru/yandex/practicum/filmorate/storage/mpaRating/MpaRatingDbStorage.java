package ru.yandex.practicum.filmorate.storage.mpaRating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public class MpaRatingDbStorage implements MpaRatingStorage {

    @Override
    public List<MpaRating> getMpaRatings() {
        return List.of();
    }

    @Override
    public MpaRating getMpaRating(int id) {
        return null;
    }
}
