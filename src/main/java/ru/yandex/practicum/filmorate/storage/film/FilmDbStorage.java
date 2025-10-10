package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

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
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        setFilmGenres(film.getGenres(), film.getId());
        log.info("Информация о фильме с id={} обновлена", film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.*, m.name as mpa FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, filmMapper);
        getAllFilmsGenres(films);
        return films;
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT f.*, m.name as mpa FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        Film film = jdbcTemplate.query(sql, filmMapper, id).stream().findFirst().orElseThrow(() ->
                new NotFoundException("Фильм с id=" + id + " не найден"));
        film.setGenres(getFilmGenres(id));
        return film;
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
        List<Film> popularFilms = (jdbcTemplate.query(sql, filmMapper, count));
        getAllFilmsGenres(popularFilms);
        return popularFilms;
    }

    private void setFilmGenres(Set<Genre> genres, int filmId) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);

        String insertFilmGenreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .toList();
        jdbcTemplate.batchUpdate(insertFilmGenreSql, batchArgs);
    }

    private Set<Genre> getFilmGenres(int id) {
        String sql = "SELECT g.* " +
                "FROM genres as g " +
                "INNER JOIN film_genre as fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sql, genreMapper, id);
        return new HashSet<>(filmGenres);
    }

    private void getAllFilmsGenres(List<Film> films) {
        String sql = "SELECT fg.film_id, g.* " +
                "FROM film_genre fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (%s) " +
                "ORDER BY fg.film_id";

        String filmIds = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.joining(","));

        String sqlWithParameter = String.format(sql, filmIds);

        final Map<Integer, Set<Genre>> genresByFilmId = new HashMap<>();

        jdbcTemplate.query(sqlWithParameter, (rs) -> {
            int filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
            genresByFilmId.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });

        films.forEach(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), Collections.emptySet())));
    }

}
