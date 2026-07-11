package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDbStorage extends BaseDbStorage<Mpa> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";

    private static final String EXISTS_QUERY = "SELECT COUNT(*) FROM mpa WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> getAllMPA() {
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa getMpaById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }

    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(EXISTS_QUERY, Integer.class, id);
        return count != null && count > 0;
    }
}