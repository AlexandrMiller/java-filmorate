package ru.yandex.practicum.filmorate.controllersTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeptions.ValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllersTest {

    FilmController filmController = new FilmController();

    @Test
    void filmDateShouldBeAfter1895() {
        LocalDate date = LocalDate.of(1000,1,1);
        Film film = Film.builder().releaseDate(date).build();
        ValidException valid = assertThrows(ValidException.class, () -> filmController.createFilm(film));
        assertEquals("Фильмов раньше 28.12.1995 года не существет",valid.getMessage());
    }

    @Test
    void filmDurationShouldBePositive() {
        Film film = Film.builder().duration(-10).build();
        ValidException valid = assertThrows(ValidException.class, () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть больше нуля",valid.getMessage());
    }
}
