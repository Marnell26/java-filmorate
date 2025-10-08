package ru.yandex.practicum.filmorate.storage.mpaRating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Repository
@Slf4j
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingRowMapper mpaMapper;

    @Autowired
    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate, MpaRatingRowMapper mpaMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
    }

    @Override
    public List<MpaRating> getMpaRatings() {
        String sql = "SELECT * FROM ratings ORDER BY id";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    @Override
    public MpaRating getMpaRating(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mpaMapper, id);
        } catch (EmptyResultDataAccessException e) {
            String message = "Рейтинг с id=" + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }

    }

}
