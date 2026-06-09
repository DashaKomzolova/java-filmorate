package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@SpringBootTest
class UserControllerTests {

    private UserController controller;
    private User validUser;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test Name");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void addUser_ShouldAddUserSuccessfully() {
        User added = controller.addFilm(validUser);

        assertNotNull(added.getId());
        assertEquals("test@example.com", added.getEmail());
        assertEquals("testlogin", added.getLogin());
        assertEquals("Test Name", added.getName());
        assertEquals(LocalDate.of(1990, 1, 1), added.getBirthday());
    }

    @Test
    void addUser_WithNullEmail_ShouldThrowException() {
        validUser.setEmail(null);

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithBlankEmail_ShouldThrowException() {
        validUser.setEmail("   ");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithEmptyEmail_ShouldThrowException() {
        validUser.setEmail("");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithEmailWithoutAtSymbol_ShouldThrowException() {
        validUser.setEmail("testexample.com");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithEmailWithAtSymbol_ShouldPass() {
        validUser.setEmail("user@mail.ru");

        User added = controller.addFilm(validUser);
        assertEquals("user@mail.ru", added.getEmail());
    }

    @Test
    void addUser_WithNullLogin_ShouldThrowException() {
        validUser.setLogin(null);

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithBlankLogin_ShouldThrowException() {
        validUser.setLogin("   ");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithEmptyLogin_ShouldThrowException() {
        validUser.setLogin("");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithLoginContainingSpaces_ShouldThrowException() {
        validUser.setLogin("test login with spaces");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithLoginWithoutSpaces_ShouldPass() {
        validUser.setLogin("valid_login");

        User added = controller.addFilm(validUser);
        assertEquals("valid_login", added.getLogin());
    }

    @Test
    void addUser_WithNullName_ShouldSetNameToLogin() {
        validUser.setName(null);

        User added = controller.addFilm(validUser);
        assertEquals("testlogin", added.getName());
    }

    @Test
    void addUser_WithBlankName_ShouldSetNameToLogin() {
        validUser.setName("   ");

        User added = controller.addFilm(validUser);
        assertEquals("testlogin", added.getName());
    }

    @Test
    void addUser_WithEmptyName_ShouldSetNameToLogin() {
        validUser.setName("");

        User added = controller.addFilm(validUser);
        assertEquals("testlogin", added.getName());
    }

    @Test
    void addUser_WithValidName_ShouldKeepName() {
        validUser.setName("Real Name");

        User added = controller.addFilm(validUser);
        assertEquals("Real Name", added.getName());
    }

    @Test
    void addUser_WithBirthdayInFuture_ShouldThrowException() {
        validUser.setBirthday(LocalDate.of(2030, 1, 1));

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithBirthdayToday_ShouldPass() {
        validUser.setBirthday(LocalDate.now());

        User added = controller.addFilm(validUser);
        assertEquals(LocalDate.now(), added.getBirthday());
    }

    @Test
    void addUser_WithBirthdayInPast_ShouldPass() {
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        User added = controller.addFilm(validUser);
        assertEquals(LocalDate.of(2000, 1, 1), added.getBirthday());
    }

    @Test
    void addUser_WithNullBirthday_ShouldPass() {
        validUser.setBirthday(null);

        User added = controller.addFilm(validUser);
        assertNull(added.getBirthday());
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully() {
        User added = controller.addFilm(validUser);

        added.setEmail("new@example.com");
        added.setLogin("newlogin");
        added.setName("New Name");
        added.setBirthday(LocalDate.of(1995, 5, 5));

        User updated = controller.updateFilm(added);

        assertEquals("new@example.com", updated.getEmail());
        assertEquals("newlogin", updated.getLogin());
        assertEquals("New Name", updated.getName());
        assertEquals(LocalDate.of(1995, 5, 5), updated.getBirthday());
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        validUser.setId(999L);

        assertThrows(NotFoundException.class, () -> {
            controller.updateFilm(validUser);
        });
    }

    @Test
    void updateUser_WithNullId_ShouldThrowException() {
        validUser.setId(null);

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.updateFilm(validUser);
        });
    }

    @Test
    void updateUser_WithPartialData_ShouldKeepOtherFields() {
        User added = controller.addFilm(validUser);

        User updateData = new User();
        updateData.setId(added.getId());
        updateData.setEmail("updated@example.com");

        User updated = controller.updateFilm(updateData);

        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("testlogin", updated.getLogin());
        assertEquals("Test Name", updated.getName());
        assertEquals(LocalDate.of(1990, 1, 1), updated.getBirthday());
    }

    @Test
    void updateUser_WithOnlyLogin_ShouldUpdateOnlyLogin() {
        User added = controller.addFilm(validUser);

        User updateData = new User();
        updateData.setId(added.getId());
        updateData.setLogin("newlogin123");

        User updated = controller.updateFilm(updateData);

        assertEquals("test@example.com", updated.getEmail());
        assertEquals("newlogin123", updated.getLogin());
        assertEquals("Test Name", updated.getName());
        assertEquals(LocalDate.of(1990, 1, 1), updated.getBirthday());
    }

    @Test
    void getUsers_ShouldReturnEmptyList_WhenNoUsers() {
        assertEquals(0, controller.getFilms().size());
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        controller.addFilm(validUser);

        User secondUser = new User();
        secondUser.setEmail("second@example.com");
        secondUser.setLogin("seconduser");
        secondUser.setName("Second User");
        secondUser.setBirthday(LocalDate.of(2000, 1, 1));
        controller.addFilm(secondUser);

        assertEquals(2, controller.getFilms().size());
    }

    @Test
    void addUser_WithInvalidEmailAndValidLogin_ShouldThrowException() {
        validUser.setEmail("invalid");
        validUser.setLogin("goodlogin");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }

    @Test
    void addUser_WithValidEmailAndInvalidLogin_ShouldThrowException() {
        validUser.setEmail("good@example.com");
        validUser.setLogin("bad login");

        assertThrows(ConditionsNotMetException.class, () -> {
            controller.addFilm(validUser);
        });
    }
}