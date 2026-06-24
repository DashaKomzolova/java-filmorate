package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Film addFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        validateReleaseDate(film.getReleaseDate());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        films.remove(id);
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public boolean existsById(Long id) {
        return films.containsKey(id);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(MIN_DATE)) {
            throw new ConditionsNotMetException("Дата релиза фильма — не раньше 28 декабря 1895 года");
        }
    }
}