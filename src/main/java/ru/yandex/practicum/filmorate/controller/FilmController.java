package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MINDATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@RequestBody Film film) {

        log.info("Запрос на добавление фильма: {}", film);

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка создать фильм без названия");
            throw new ConditionsNotMetException("Название должно быть указано");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ConditionsNotMetException("Описание должно быть максимум 200 символов");
        }

        if (film.getReleaseDate().isBefore(MINDATE)) {
            log.warn("Неверная дата релиза: {}", film.getReleaseDate());
            throw new ConditionsNotMetException("Дата релиза фильма — не раньше 28 декабря 1895 года;");
        }

        if (film.getDuration() <= 0) {
            log.warn("Отрицательная или нулевая длительность фильма: {}", film.getDuration());
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен с id={}", film.getId());

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {

        log.info("Запрос на обновление фильма: {}", newFilm);

        if (newFilm.getId() == null) {
            log.warn("Попытка обновления фильма без id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        Film oldFilm = films.get(newFilm.getId());

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() > 0) {
            oldFilm.setDuration(newFilm.getDuration());
        }

        log.info("Фильм с id={} успешно обновлён", newFilm.getId());

        return oldFilm;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Запрос на получение всех фильмов");
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
