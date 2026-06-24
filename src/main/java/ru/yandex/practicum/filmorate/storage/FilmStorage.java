package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilm(Long id);
    Film getFilmById(Long id);  // вместо Optional<Film>
    Collection<Film> getAllFilms();
    boolean existsById(Long id);
}