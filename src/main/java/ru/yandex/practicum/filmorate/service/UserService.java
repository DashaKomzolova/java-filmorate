package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Добавление пользователя: {}", user.getLogin());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {

        Optional<User> existingUser = userStorage.getUserById(user.getId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        log.info("Обновление пользователя с id={}", user.getId());
        return userStorage.updateUser(user);
    }

    public Optional<User> getUserById(Long id) {
        log.info("Получение пользователя с id={}", id);
        return userStorage.getUserById(id);
    }

    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(otherId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }
}