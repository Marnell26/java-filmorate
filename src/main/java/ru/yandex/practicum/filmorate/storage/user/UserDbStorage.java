package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userMapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO user (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Пользователь с id={} добавлен", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE user SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId());
            log.info("Информация о пользователе {} обновлена", user.getId());
        } catch (EmptyResultDataAccessException exception) {
            String message = "Пользователь с id=" + user.getId() + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM user";
        return new ArrayList<>(jdbcTemplate.query(sql, userMapper));
    }

    @Override
    public User getUser(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userMapper, id);
        } catch (EmptyResultDataAccessException exception) {
            String message = "Пользователь с id=" + id + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friend (user_id, friend_id) VALUES (?, ?)";
        List<Integer> friendsIds = getFriends(userId).stream().map(User::getId).toList();
        if (!friendsIds.contains(friendId)) {
            jdbcTemplate.update(sql, getUser(userId).getId(), getUser(friendId).getId());
            log.info("Пользователь {} добавил пользователя {} в друзья", userId, friendId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friend WHERE user_id = ? AND friend_id = ?";
        List<Integer> friendsIds = getFriends(userId).stream().map(User::getId).toList();
        if (!friendsIds.contains(friendId)) {
            jdbcTemplate.update(sql, getUser(userId).getId(), getUser(friendId).getId());
            log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int id) {
        String sql = "SELECT * FROM user AS u LEFT JOIN friend AS f ON u.id = f.friend_id WHERE f.user_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sql, userMapper, getUser(id).getId()));
    }

}
