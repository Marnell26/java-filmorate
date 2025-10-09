package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
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
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
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
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        try {
            getUser(user.getId());
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
        String sql = "SELECT * FROM users";
        return new ArrayList<>(jdbcTemplate.query(sql, userMapper));
    }

    @Override
    public User getUser(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
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
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        List<Integer> friendsIds = getFriends(userId).stream().map(User::getId).toList();
        if (!friendsIds.contains(friendId)) {
            jdbcTemplate.update(sql, getUser(userId).getId(), getUser(friendId).getId());
            log.info("Пользователь {} добавил пользователя {} в друзья", userId, friendId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        List<Integer> friendsIds = getFriends(getUser(userId).getId()).stream().map(User::getId).toList();
        if (friendsIds.contains(getUser(friendId).getId())) {
            jdbcTemplate.update(sql, userId, friendId);
            log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
        }

    }

    @Override
    public List<User> getFriends(int id) {
        String sql = "SELECT * FROM users AS u LEFT JOIN friendships AS f ON u.id = f.friend_id WHERE f.user_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sql, userMapper, getUser(id).getId()));
    }

    @Override
    public List<User> getCommonFriends(int id, int friendId) {
        String sql = "SELECT * " +
                "FROM users u " +
                "LEFT JOIN friendships f1 ON u.id = f1.friend_id " +
                "LEFT JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sql, userMapper, getUser(id).getId(), getUser(friendId).getId()));
    }

}
