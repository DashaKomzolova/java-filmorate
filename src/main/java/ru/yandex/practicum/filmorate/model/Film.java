package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название должно быть указано")
    private String name;

    @Size(max = 200, message = "Описание должно быть максимум 200 символов")

    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}
