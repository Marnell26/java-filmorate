package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public void createFriendship(User user, User friendUser) {
        if (userStorage.getUser(user.getId()) == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        if (userStorage.getUser(friendUser.getId()) == null) {
            log.error("Пользователь, добавляемый в друзья, не найден");
            throw new NotFoundException("Пользователь, добавляемый в друзья, не найден");
        }
        user.setFriendId(friendUser.getId());
        friendUser.setFriendId(user.getId());
        log.info("Пользователи {} и {} теперь друзья", user.getId(), friendUser.getId());
    }

    public void removeFriendship(User user, User friendUser) {
        if (userStorage.getUser(user.getId()) == null || userStorage.getUser(friendUser.getId()) == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        if (!user.getFriendsIds().contains(friendUser.getId())) {
            log.error("Пользователь не найден в списке друзей");
        }
        user.removeFriendId(friendUser.getId());
        friendUser.removeFriendId(user.getId());
        log.info("Пользователи {} и {} больше не являются друзьями", user.getId(), friendUser.getId());
    }

    public List<User> getFriends(User user) {
        return user.getFriendsIds()
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(User user, User friendUser) {
        return Stream.concat(getFriends(user).stream(), getFriends(friendUser).stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
