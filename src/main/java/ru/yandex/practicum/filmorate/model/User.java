package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class User {

    private Long id;

    @NotBlank(message = "Почта должна быть указана")
    @Email(message = "Почта должна содержать @")
    private String email;

    @NotBlank(message = "Логин должен быть указан")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Map<Long, FriendshipStatus> friends = new HashMap<>();

    public void addFriend(Long friendId) {
        friends.put(friendId, FriendshipStatus.PENDING);
    }

    public void confirmFriend(Long friendId) {
        friends.put(friendId, FriendshipStatus.CONFIRMED);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public boolean isFriend(Long userId) {
        return friends.containsKey(userId);
    }

    public Set<Long> getCommonFriends(User otherUser) {
        Set<Long> commonFriends = new java.util.HashSet<>(this.friends.keySet());
        commonFriends.retainAll(otherUser.getFriends().keySet());
        return commonFriends;
    }
}