package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreMapper;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = genreMapper;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, genreMapper);
    }

    @Override
    public Genre getGenre(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        Genre genre = jdbcTemplate.queryForObject(sql, genreMapper, id);
        if (genre == null) {
            String message = "Жанр с id=" + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return genre;
    }

}
