package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);

        if (!friends.containsKey(userId)) {
            friends.put(userId, new HashSet<>());
        }

        friends.get(userId).add(friendId);

        if (!friends.containsKey(friendId)) {
            friends.put(friendId, new HashSet<>());
        }

        friends.get(friendId).add(userId);

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);

        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
        }

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Set<Long> friendIds = friends.get(userId);
        if (friendIds == null || friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> userFriends = new ArrayList<>();
        for (Long friendId : friendIds) {
            User friend = userStorage.getUserById(friendId);
            userFriends.add(friend);
        }

        return userFriends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!userStorage.existsById(otherId)) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }

        Set<Long> userFriends = friends.get(userId);
        Set<Long> otherFriends = friends.get(otherId);

        if (userFriends == null || userFriends.isEmpty() ||
                otherFriends == null || otherFriends.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : userFriends) {
            if (otherFriends.contains(friendId)) {
                User friend = userStorage.getUserById(friendId);
                commonFriends.add(friend);
            }
        }

        return commonFriends;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    private void validateUsersExist(Long userId, Long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!userStorage.existsById(friendId)) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }
    }
}