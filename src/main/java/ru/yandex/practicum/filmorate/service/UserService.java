package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void createFriendship(User user, User friendUser) {
        user.setFriendId(friendUser.getId());
        friendUser.setFriendId(user.getId());
    }

    public void removeFriendship(User user, User friendUser) {
        user.removeFriendId(friendUser.getId());
        friendUser.removeFriendId(user.getId());
    }

    public List<User> getFriends(User user) {
        return user.getFriendsIds()
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(User user, User friendUser) {
        return user.getFriendsIds()
                .stream()
                .map(userStorage::getUser)
                .filter()
                .collect(Collectors.toList());
    }

}
