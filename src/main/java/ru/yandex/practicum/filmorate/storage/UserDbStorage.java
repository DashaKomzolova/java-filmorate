package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";

    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? "
                                                + "WHERE id = ?";

    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String ADD_FRIEND_QUERY =
            "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";

    private static final String REMOVE_FRIEND_QUERY =
            "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    private static final String FIND_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN friends f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ?";

    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN friends f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
                    "JOIN friends f2 ON u.id = f2.friend_id AND f2.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        delete(DELETE_USER_QUERY, id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public boolean existsById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).isPresent();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId, "CONFIRMED");
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return findMany(FIND_FRIENDS_QUERY, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, userId, otherId);
    }
}