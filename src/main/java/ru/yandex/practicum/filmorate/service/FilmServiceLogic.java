package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceLogic implements FilmService {

    private final FilmStorage filmStorage;

    @Override
    public Film createFilm(Film film) throws IllegalAccessException {
        filmStorage.createFilm(film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        Long id = film.getId();
        Optional<Film> optionalFilm = filmStorage.findById(id);
        if (optionalFilm.isEmpty()) {
            String errorMessage = String.format("Фильм с таким ID не найден");
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        filmStorage.save(film);

        return film;
    }


    @Override
    public List<Film> getFilms() {
        if (filmStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Список фильмов пуст");
        } else {
            return filmStorage.getFilms();
        }
    }


    @Override
    public Set<Long> addLike(Long filmId,Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new ValidException("Фильм не найден"));

        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
            film.setLikes(likes);
        }
        if (likes.add(userId)) {
            filmStorage.save(film);
        }
        return likes;
    }

    @Override
    public void deleteLike(Long filmId,Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Set<Long> likes = film.getLikes();

        if (!(likes.contains(userId))) {
            throw new NotFoundException("Фильм не лайкнут");
        }
        likes.remove(userId);
    }

    @Override
    public List<Film> getMostLikedFilms(Integer count) {
        List<Film> films = filmStorage.getFilms();
        if (films == null) {
            return Collections.emptyList();
        }
        if (count != null) {
            return films.stream()
                    .sorted(Comparator.comparingInt((Film f) -> f.getLikes() != null ? f.getLikes().size() : 0).reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return films.stream()
                    .sorted(Comparator.comparingInt((Film f) -> f.getLikes() != null ? f.getLikes().size() : 0).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        }
    }




}
