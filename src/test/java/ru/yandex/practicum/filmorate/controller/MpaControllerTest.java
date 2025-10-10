package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaControllerTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void getAllMpaTest() {
        List<Mpa> mpaRatings = mpaDbStorage.getAllMpa();

        assertThat(mpaRatings).hasSize(5);
        assertThat(mpaRatings).extracting(Mpa::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    public void getMpaByIdTest() {
        Mpa mpa = mpaDbStorage.getMpa(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

}
