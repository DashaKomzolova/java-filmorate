package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
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

    private Set<Long> friends = new HashSet<>();

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public boolean isFriend(Long userId) {
        return friends.contains(userId);
    }

    public Set<Long> getCommonFriends(User otherUser) {
        Set<Long> commonFriends = new HashSet<>(this.friends);
        commonFriends.retainAll(otherUser.getFriends());
        return commonFriends;
    }
}