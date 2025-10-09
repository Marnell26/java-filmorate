package ru.yandex.practicum.filmorate.storage.mpaRating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaMapper;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mpaMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    @Override
    public Mpa getMpa(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sql, mpaMapper, id);
        if (mpa == null) {
            String message = "Рейтинг с id=" + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return mpa;
    }

}
