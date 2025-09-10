package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public void createFriendship(int id, int friendId) {
        getUser(id).setFriendId(getUser(friendId).getId());
        getUser(friendId).setFriendId(getUser(id).getId());
        log.info("Пользователи {} и {} теперь друзья", id, friendId);
    }

    public void removeFriendship(int id, int friendId) {
        if (!getUser(id).getFriendsIds().contains(getUser(friendId).getId())) {
            log.error("Пользователь не найден в списке друзей");
        }
        getUser(id).removeFriendId(friendId);
        getUser(friendId).removeFriendId(id);
        log.info("Пользователи {} и {} больше не являются друзьями", id, friendId);
    }

    public List<User> getFriends(int id) {
        return getUser(id).getFriendsIds()
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int friendId) {
        return Stream.concat(getFriends(getUser(id).getId()).stream(), getFriends(getUser(friendId).getId()).stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
