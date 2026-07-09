package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDbStorage extends BaseDbStorage<Genre> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String EXISTS_QUERY = "SELECT COUNT(*) FROM genre WHERE id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenreById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }

    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(EXISTS_QUERY, Integer.class, id);
        return count != null && count > 0;
    }
}