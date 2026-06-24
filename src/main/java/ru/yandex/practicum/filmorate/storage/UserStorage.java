package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {
    User addUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    User getUserById(Long id);  // вместо Optional<User>
    Collection<User> getAllUsers();
    boolean existsById(Long id);
}