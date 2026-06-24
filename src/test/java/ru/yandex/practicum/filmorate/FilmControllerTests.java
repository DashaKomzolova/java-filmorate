//package ru.yandex.practicum.filmorate;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.controller.FilmController;
//import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.time.LocalDate;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FilmControllerTests {
//
//	private FilmController controller;
//	private Film validFilm;
//
//	private Validator validator;
//
//	@BeforeEach
//	void setUp() {
//		controller = new FilmController();
//
//		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//		validator = factory.getValidator();
//
//		validFilm = new Film();
//		validFilm.setName("Test Film");
//		validFilm.setDescription("Test Description");
//		validFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
//		validFilm.setDuration(80);
//	}
//
//	@Test
//	void validation_emptyName_shouldFail() {
//		validFilm.setName("");
//
//		Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
//
//		assertFalse(violations.isEmpty());
//	}
//
//	@Test
//	void validation_nullName_shouldFail() {
//		validFilm.setName(null);
//
//		Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
//
//		assertFalse(violations.isEmpty());
//	}
//
//	@Test
//	void validation_descriptionTooLong_shouldFail() {
//		validFilm.setDescription("a".repeat(201));
//
//		Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
//
//		assertFalse(violations.isEmpty());
//	}
//
//	@Test
//	void validation_negativeDuration_shouldFail() {
//		validFilm.setDuration(-10);
//
//		Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
//
//		assertFalse(violations.isEmpty());
//	}
//
//	@Test
//	void validation_zeroDuration_shouldFail() {
//		validFilm.setDuration(0);
//
//		Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
//
//		assertFalse(violations.isEmpty());
//	}
//
//	@Test
//	void validation_validFilm_shouldPass() {
//		Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
//
//		assertTrue(violations.isEmpty());
//	}
//
//	@Test
//	void addFilm_shouldAddSuccessfully() {
//		Film added = controller.addFilm(validFilm);
//
//		assertNotNull(added.getId());
//		assertEquals("Test Film", added.getName());
//	}
//
//	@Test
//	void addFilm_releaseDateTooEarly_shouldThrowException() {
//		validFilm.setReleaseDate(LocalDate.of(1800, 1, 1));
//
//		assertThrows(ConditionsNotMetException.class, () -> {
//			controller.addFilm(validFilm);
//		});
//	}
//
//	@Test
//	void updateFilm_shouldUpdateSuccessfully() {
//		Film added = controller.addFilm(validFilm);
//
//		added.setName("Updated");
//		Film updated = controller.updateFilm(added);
//
//		assertEquals("Updated", updated.getName());
//	}
//
//	@Test
//	void updateFilm_notFound_shouldThrowException() {
//		validFilm.setId(999L);
//
//		assertThrows(NotFoundException.class, () -> {
//			controller.updateFilm(validFilm);
//		});
//	}
//
//	@Test
//	void updateFilm_nullId_shouldThrowException() {
//		validFilm.setId(null);
//
//		assertThrows(ConditionsNotMetException.class, () -> {
//			controller.updateFilm(validFilm);
//		});
//	}
//
//	@Test
//	void getFilms_shouldReturnAllFilms() {
//		controller.addFilm(validFilm);
//
//		Film second = new Film();
//		second.setName("Second");
//		second.setDescription("Desc");
//		second.setReleaseDate(LocalDate.of(2021, 1, 1));
//		second.setDuration(90);
//
//		controller.addFilm(second);
//
//		assertEquals(2, controller.getFilms().size());
//	}
//}
