package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.yandex.practicum.filmorate.annotations.BirthdayValid;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString
public class User {

    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;

    @BirthdayValid(message = "Нельзя родиться в будущем :)")
    private LocalDate birthday;
}
