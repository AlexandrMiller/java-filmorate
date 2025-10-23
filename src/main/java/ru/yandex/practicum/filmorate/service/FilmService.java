package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmService {

    Film createFilm(Film film) throws IllegalAccessException;

    List<Film> getFilms();

    Film updateFilm(Film film);

    Set<Long> addLike(Long filmId, Long userId);

    List<Film> getMostLikedFilms(Integer count);

    void deleteLike(Long filmId,Long userId);
}
