package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTests {

    private UserController controller;
    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void invalidEmail_shouldFail() {
        user.setEmail("bad");

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void loginWithSpaces_shouldFail() {
        user.setLogin("bad login");

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void futureBirthday_shouldFail() {
        user.setBirthday(LocalDate.of(3000, 1, 1));

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void addUser_shouldWork() {
        assertNotNull(controller.addUser(user).getId());
    }

    @Test
    void updateUser_shouldUpdate() {
        User created = controller.addUser(user);

        User update = new User();
        update.setId(created.getId());
        update.setEmail("new@mail.com");

        assertEquals("new@mail.com",
                controller.updateUser(update).getEmail());
    }

    @Test
    void updateUser_shouldFail_noId() {
        assertThrows(ConditionsNotMetException.class,
                () -> controller.updateUser(new User()));
    }
}
