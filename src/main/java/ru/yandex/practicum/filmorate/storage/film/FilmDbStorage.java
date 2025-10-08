package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmMapper;
    private final MpaRatingRowMapper mpaMapper;
    private final GenreRowMapper genreMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmMapper, MpaRatingRowMapper mpaMapper,
                         GenreRowMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.mpaMapper = mpaMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpaRating().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        setGenres(film.getGenres(), film.getId());
        log.info("Фильм с id={} добавлен", film.getId());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpaRating().getId(),
                    film.getId());
            setGenres(film.getGenres(), film.getId());
        } catch (EmptyResultDataAccessException exception) {
            String message = "Фильм с id=" + film.getId() + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }

        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM film";
        return new ArrayList<>(jdbcTemplate.query(sql, filmMapper));
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, filmMapper, id);
        } catch (EmptyResultDataAccessException exception) {
            String message = "Пользователь с id=" + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO like (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Лайк к фильму {} добавлен пользователем {}", filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM like WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Лайк к фильму {} удален пользователем {}", filmId, userId);
    }

    private void setGenres(Set<Genre> genres, int filmId) {
        String findGenresSql = "SELECT id FROM genres";
        String insertFilmGenreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        List<Integer> genresId = jdbcTemplate.queryForList(findGenresSql, Integer.class);

        for (Genre genre : genres) {
            if (genresId.contains(genre.getId())) {
                if (!isFilmGenreAlreadyExist(filmId, genre.getId())) {
                    jdbcTemplate.update(insertFilmGenreSql, filmId, genre.getId());
                }
            }
        }
    }

}
