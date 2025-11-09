package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotations.DateAndDurationValid;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Length(max = 200,message = "Описание фильма не должно превышать 200 символов")
    private String description;

    @DateAndDurationValid(message = "Дата релиза не может быть раньше 28.12.1895")
    private LocalDate releaseDate;

    @DateAndDurationValid(message = "Продолжительность фильма должна быть больше нуля")
    private Integer duration;
    private Set<Long> likes;
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private Mpa mpa;
}
