package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User addFilm(@RequestBody User user) {

        log.info("Запрос на создание пользователя: {}", user);

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Email пустой");
            throw new ConditionsNotMetException("Почта должна быть указана");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Email без @: {}", user.getEmail());
            throw new ConditionsNotMetException("Почта должна содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Логин пустой");
            throw new ConditionsNotMetException("Логин должен быть указан");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Логин содержит пробелы: {}", user.getLogin());
            throw new ConditionsNotMetException("Логин не должен содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пустое, заменяем на login: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", user.getBirthday());
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан с id={}", user.getId());

        return user;
    }

    @PutMapping
    public User updateFilm(@RequestBody User newUser) {

        log.info("Запрос на обновление пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.warn("Попытка обновления без id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Пользователь не найден id={}", newUser.getId());
            throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }

        log.info("Пользователь обновлён id={}", newUser.getId());

        return oldUser;
    }

    @GetMapping
    public Collection<User> getFilms() {
        log.info("Запрос на получение пользователей");
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
