package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, m.name AS mpa_name FROM film f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "WHERE f.id = ?";

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, m.name AS mpa_name FROM film f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id";

    private static final String INSERT_QUERY = "INSERT INTO film(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";

    private static final String DELETE_MOVIE_GENRE_QUERY = "DELETE FROM movie_genre WHERE film_id = ?";

    private static final String DELETE_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ?";

    private static final String DELETE_FILM_QUERY = "DELETE FROM film WHERE id = ?";

    private static final String EXISTS_QUERY = "SELECT COUNT(*) FROM film WHERE id = ?";

    private static final String FIND_GENRES_BY_FILM_ID_QUERY =
            "SELECT g.id, g.name FROM genre g " +
                    "JOIN movie_genre mg ON g.id = mg.genre_id " +
                    "WHERE mg.film_id = ? " +
                    "ORDER BY g.id";

    private static final String INSERT_MOVIE_GENRE_QUERY =
            "INSERT INTO movie_genre (film_id, genre_id) VALUES (?, ?)";

    private static final String ADD_LIKE_QUERY =
            "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_POPULAR_QUERY =
            "SELECT f.*, m.name AS mpa_name FROM film f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "GROUP BY f.id, m.name " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";

    private final RowMapper<Genre> genreMapper;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, RowMapper<Genre> genreMapper) {
        super(jdbc, mapper);
        this.genreMapper = genreMapper;
    }

    @Override
    public Film addFilm(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );
        film.setId(id);
        saveGenres(film);
        return getFilmById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );
        jdbc.update(DELETE_MOVIE_GENRE_QUERY, film.getId());
        saveGenres(film);
        return getFilmById(film.getId());
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            jdbc.update(INSERT_MOVIE_GENRE_QUERY, film.getId(), genre.getId());
        }
    }

    @Override
    public void deleteFilm(Long id) {
        jdbc.update(DELETE_MOVIE_GENRE_QUERY, id);
        jdbc.update(DELETE_LIKES_QUERY, id);
        delete(DELETE_FILM_QUERY, id);
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
        film.setGenres(loadGenres(id));
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        for (Film film : films) {
            film.setGenres(loadGenres(film.getId()));
        }
        return films;
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(EXISTS_QUERY, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = jdbc.query(FIND_POPULAR_QUERY, mapper, count);
        for (Film film : films) {
            film.setGenres(loadGenres(film.getId()));
        }
        return films;
    }

    private Set<Genre> loadGenres(Long filmId) {
        List<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, genreMapper, filmId);
        return new LinkedHashSet<>(genres);
    }
}