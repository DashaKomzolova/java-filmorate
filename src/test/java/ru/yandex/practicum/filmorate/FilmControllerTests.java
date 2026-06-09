package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@SpringBootTest
class FilmControllerTests {

	private FilmController controller;
	private Film validFilm;

	@BeforeEach
	void setUp() {
		controller = new FilmController();
		validFilm = new Film();
		validFilm.setName("Test Film");
		validFilm.setDescription("Test Description");
		validFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
		validFilm.setDuration(80);
	}

	@Test
	void addFilm_ShouldAddFilmSuccessfully() {
		Film added = controller.addFilm(validFilm);

		assertNotNull(added.getId());
		assertEquals("Test Film", added.getName());
		assertEquals(80, added.getDuration());
	}

	@Test
	void addFilm_WithEmptyName_ShouldThrowException() {
		validFilm.setName("");

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.addFilm(validFilm);
		});
	}

	@Test
	void addFilm_WithNullName_ShouldThrowException() {
		validFilm.setName(null);

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.addFilm(validFilm);
		});
	}

	@Test
	void addFilm_WithDescriptionLongerThan200_ShouldThrowException() {
		validFilm.setDescription("a".repeat(201));

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.addFilm(validFilm);
		});
	}

	@Test
	void addFilm_WithReleaseDateBefore1895_ShouldThrowException() {
		validFilm.setReleaseDate(LocalDate.of(1800, 1, 1));

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.addFilm(validFilm);
		});
	}

	@Test
	void addFilm_WithNegativeDuration_ShouldThrowException() {
		validFilm.setDuration(-10);

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.addFilm(validFilm);
		});
	}

	@Test
	void addFilm_WithZeroDuration_ShouldThrowException() {
		validFilm.setDuration(0);

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.addFilm(validFilm);
		});
	}

	@Test
	void updateFilm_ShouldUpdateSuccessfully() {
		Film added = controller.addFilm(validFilm);

		added.setName("Updated Name");
		Film updated = controller.updateFilm(added);

		assertEquals("Updated Name", updated.getName());
	}

	@Test
	void updateFilm_WithNonExistentId_ShouldThrowException() {
		validFilm.setId(999L);

		assertThrows(NotFoundException.class, () -> {
			controller.updateFilm(validFilm);
		});
	}

	@Test
	void updateFilm_WithNullId_ShouldThrowException() {
		validFilm.setId(null);

		assertThrows(ConditionsNotMetException.class, () -> {
			controller.updateFilm(validFilm);
		});
	}

	@Test
	void getFilms_ShouldReturnAllFilms() {
		controller.addFilm(validFilm);

		Film secondFilm = new Film();
		secondFilm.setName("Second");
		secondFilm.setDescription("Desc");
		secondFilm.setReleaseDate(LocalDate.of(2021, 1, 1));
		secondFilm.setDuration(90);
		controller.addFilm(secondFilm);

		assertEquals(2, controller.getFilms().size());
	}
}
