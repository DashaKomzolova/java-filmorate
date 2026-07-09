package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        UserDbStorage.class, UserRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        MPA mpa = new MPA();
        mpa.setId(1L);
        film.setMpa(mpa);

        return film;
    }

    @Test
    public void testAddUser() {
        User user = createTestUser();

        User savedUser = userStorage.addUser(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser).hasFieldOrPropertyWithValue("email", "test@mail.com");
        assertThat(savedUser).hasFieldOrPropertyWithValue("login", "testlogin");
        assertThat(savedUser).hasFieldOrPropertyWithValue("name", "Test User");
    }

    @Test
    public void testFindUserById() {
        User savedUser = userStorage.addUser(createTestUser());

        Optional<User> userOptional = userStorage.getUserById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", savedUser.getId())
                );
    }

    @Test
    public void testFindUserByIdNotFound() {
        Optional<User> userOptional = userStorage.getUserById(999L);

        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testGetAllUsers() {
        userStorage.addUser(createTestUser());

        User secondUser = createTestUser();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("secondlogin");
        userStorage.addUser(secondUser);

        List<User> users = userStorage.getAllUsers();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void testUpdateUser() {
        User savedUser = userStorage.addUser(createTestUser());

        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@mail.com");
        userStorage.updateUser(savedUser);

        Optional<User> updatedUser = userStorage.getUserById(savedUser.getId());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("name", "Updated Name");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "updated@mail.com");
                });
    }

    @Test
    public void testDeleteUser() {
        User savedUser = userStorage.addUser(createTestUser());

        userStorage.deleteUser(savedUser.getId());

        Optional<User> deletedUser = userStorage.getUserById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    public void testUserExistsByIdTrue() {
        User savedUser = userStorage.addUser(createTestUser());

        boolean exists = userStorage.existsById(savedUser.getId());

        assertThat(exists).isTrue();
    }

    @Test
    public void testUserExistsByIdFalse() {
        boolean exists = userStorage.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    public void testAddFilm() {
        Film film = createTestFilm();

        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm).hasFieldOrPropertyWithValue("name", "Test Film");
        assertThat(savedFilm).hasFieldOrPropertyWithValue("description", "Test description");
        assertThat(savedFilm).hasFieldOrPropertyWithValue("duration", 120);
    }

    @Test
    public void testFindFilmById() {
        Film savedFilm = filmStorage.addFilm(createTestFilm());

        Film foundFilm = filmStorage.getFilmById(savedFilm.getId());

        assertThat(foundFilm).hasFieldOrPropertyWithValue("id", savedFilm.getId());
        assertThat(foundFilm).hasFieldOrPropertyWithValue("name", "Test Film");
        assertThat(foundFilm.getMpa()).isNotNull();
        assertThat(foundFilm.getMpa().getId()).isEqualTo(1L);
    }

    @Test
    public void testFindFilmByIdNotFound() {
        assertThatThrownBy(() -> filmStorage.getFilmById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testGetAllFilms() {
        filmStorage.addFilm(createTestFilm());

        Film secondFilm = createTestFilm();
        secondFilm.setName("Second Film");
        filmStorage.addFilm(secondFilm);

        List<Film> films = (List<Film>) filmStorage.getAllFilms();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void testUpdateFilm() {
        Film savedFilm = filmStorage.addFilm(createTestFilm());

        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated description");
        savedFilm.setDuration(150);
        filmStorage.updateFilm(savedFilm);

        Film updatedFilm = filmStorage.getFilmById(savedFilm.getId());

        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Updated Film");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "Updated description");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", 150);
    }

    @Test
    public void testDeleteFilm() {
        Film savedFilm = filmStorage.addFilm(createTestFilm());

        filmStorage.deleteFilm(savedFilm.getId());

        assertThatThrownBy(() -> filmStorage.getFilmById(savedFilm.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testFilmExistsByIdTrue() {
        Film savedFilm = filmStorage.addFilm(createTestFilm());

        boolean exists = filmStorage.existsById(savedFilm.getId());

        assertThat(exists).isTrue();
    }

    @Test
    public void testFilmExistsByIdFalse() {
        boolean exists = filmStorage.existsById(999L);

        assertThat(exists).isFalse();
    }
}