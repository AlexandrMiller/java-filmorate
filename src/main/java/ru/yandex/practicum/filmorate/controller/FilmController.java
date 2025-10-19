package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Validator.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long,Film> filmById = new HashMap<>();
    private Long filmId = 1L;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws IllegalAccessException {
        log.info("Получен запрос на создание фильма");
        Long id = generateId();
        film.setId(id);
        FilmValidator.validate(film);
        filmById.put(id,film);

        log.info("Выполнен запрос на создание фильма");
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Выполен запрос на получение фильмов");
        return new ArrayList<>(filmById.values());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление информации о фильме");
        Long id = film.getId();
        Film currentFilm = filmById.get(id);

        if (Objects.isNull(currentFilm)) {
            String errorMessage = String.format("Фильм с таким ID не найден");
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        filmById.put(id,film);

        log.info("Выполен запрос на обновление фильма");
        return film;
    }

    public Long generateId() {
        return filmId++;
    }
}
