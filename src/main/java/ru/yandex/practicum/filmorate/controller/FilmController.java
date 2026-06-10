package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма: {}", film);

        validateReleaseDate(film.getReleaseDate());

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен с id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Запрос на обновление фильма: {}", newFilm);

        if (newFilm.getId() == null) {
            log.warn("Попытка обновления фильма без id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Film existingFilm = films.get(newFilm.getId());
        if (existingFilm == null) {
            log.warn("Фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) {
            existingFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            existingFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            validateReleaseDate(newFilm.getReleaseDate());
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() > 0) {
            existingFilm.setDuration(newFilm.getDuration());
        }

        log.info("Фильм с id={} успешно обновлён", newFilm.getId());
        return existingFilm;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Запрос на получение всех фильмов");
        return films.values();
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(MIN_DATE)) {
            log.warn("Неверная дата релиза: {}", releaseDate);
            throw new ConditionsNotMetException("Дата релиза фильма — не раньше 28 декабря 1895 года");
        }
    }

    private long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}