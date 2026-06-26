package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;  // ДОБАВИТЬ
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {

        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
            likes.put(filmId, filmLikes);
        }

        filmLikes.add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {

        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
            log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null || count <= 0) {
            count = 10;
        }

        Collection<Film> allFilms = filmStorage.getAllFilms();
        List<Film> filmsList = new ArrayList<>(allFilms);

        for (int i = 0; i < filmsList.size() - 1; i++) {
            for (int j = 0; j < filmsList.size() - i - 1; j++) {
                Film film1 = filmsList.get(j);
                Film film2 = filmsList.get(j + 1);

                Set<Long> likes1 = likes.get(film1.getId());
                Set<Long> likes2 = likes.get(film2.getId());

                int likesCount1 = (likes1 == null) ? 0 : likes1.size();
                int likesCount2 = (likes2 == null) ? 0 : likes2.size();

                if (likesCount1 < likesCount2) {
                    filmsList.set(j, film2);
                    filmsList.set(j + 1, film1);
                }
            }
        }

        List<Film> popularFilms = new ArrayList<>();
        int limit = Math.min(count, filmsList.size());
        for (int i = 0; i < limit; i++) {
            popularFilms.add(filmsList.get(i));
        }

        log.info("Возвращено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}