package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmMapper;
    private final MpaRowMapper mpaMapper;
    private final GenreRowMapper genreMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmMapper, MpaRowMapper mpaMapper,
            GenreRowMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.mpaMapper = mpaMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        setFilmGenres(film.getGenres(), film.getId());
        log.info("Фильм с id={} добавлен", film.getId());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id" +
                " = ?";
        try {
            getFilm(film.getId());
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            setFilmGenres(film.getGenres(), film.getId());
        } catch (EmptyResultDataAccessException exception) {
            String message = "Фильм с id=" + film.getId() + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }

        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.*, m.name as mpa FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        return new ArrayList<>(jdbcTemplate.query(sql, filmMapper));
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT f.*, m.name as mpa FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        Film film = jdbcTemplate.queryForObject(sql, filmMapper, id);
        film.setGenres(getFilmGenres(id));
        try {
            return film;
        } catch (EmptyResultDataAccessException exception) {
            String message = "Фильм с id=" + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "MERGE INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("Лайк к фильму {} добавлен пользователем {}", filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Лайк к фильму {} удален пользователем {}", filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name as mpa " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.user_id) " +
                "DESC LIMIT ?";
        return new ArrayList<>(jdbcTemplate.query(sql, filmMapper, count));
    }

    private void setFilmGenres(Set<Genre> genres, int filmId) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);
        String insertFilmGenreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
                jdbcTemplate.update(insertFilmGenreSql, filmId, genre.getId());
        }
    }

    private Set<Genre> getFilmGenres(int id) {
        String sql = "SELECT g.* " +
                "FROM genres as g " +
                "INNER JOIN film_genre as fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sql, genreMapper, id);
        return new HashSet<>(filmGenres);
    }

}
