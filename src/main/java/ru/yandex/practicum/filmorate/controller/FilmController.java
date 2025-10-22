package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private  FilmService filmService;

    @Autowired
    public void setFilmService(FilmService filmService) {
        this.filmService = filmService;
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws IllegalAccessException {
        log.info("Получен запрос на создание фильма");
        filmService.createFilm(film);
        log.info("Выполнен запрос на создание фильма");

        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Выполен запрос на получение фильмов");
        return filmService.getFilms();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление информации о фильме");
        filmService.updateFilm(film);
        log.info("Выполен запрос на обновление фильма");

        return film;
    }

    @GetMapping("/like")
    public List<Film> getLikes(@RequestParam(value = "count",required = false) Integer count) {
        log.info("Запрос на получение MostLikedFilms");
       return filmService.getMostLikedFilms(count);
    }

    @PutMapping("/{id}/like/{filmid}")
    public Set<Long> addLike(@PathVariable("filmid") Long filmId, @PathVariable("id") Long userId) {
        log.info("Запрос на добавления лайка");
        return filmService.addLike(filmId,userId);
    }

    @DeleteMapping("/{id}/like/{filmid}")
    public void deleteLike(@PathVariable("filmid") Long filmId,@PathVariable("id") Long userId) {
        log.info("Запрос на удаления лайка");
        filmService.deleteLike(filmId,userId);
    }

}
