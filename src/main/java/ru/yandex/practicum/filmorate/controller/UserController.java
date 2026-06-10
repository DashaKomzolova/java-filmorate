package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пустое, заменяем на login: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан с id={}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Запрос на обновление пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.warn("Попытка обновления без id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            log.warn("Пользователь не найден id={}", newUser.getId());
            throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null) {
            existingUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            existingUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            existingUser.setName(newUser.getName());
        }

        if (newUser.getName() == null && existingUser.getName() == null && newUser.getLogin() != null) {
            existingUser.setName(newUser.getLogin());
        }

        if (newUser.getBirthday() != null) {
            existingUser.setBirthday(newUser.getBirthday());
        }

        log.info("Пользователь обновлён id={}", newUser.getId());
        return existingUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Запрос на получение пользователей");
        return users.values();
    }

    private long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}