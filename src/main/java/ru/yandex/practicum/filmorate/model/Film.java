package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;


    @Length(max = 200,message = "Описание фильма не должно превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
