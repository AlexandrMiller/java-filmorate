package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.IllegalStatemantException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceLogic implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film createFilm(Film film) throws IllegalAccessException {
        filmStorage.createFilm(film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {

        if (film == null) {
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
    public Set<Long> addLike(Long filmId, Long userId) {

        if (filmId == null || userId == null) {
            throw new IllegalStatemantException("Оба ID должны быть указаны");
        }
        if (filmId <= 0 || userId <= 0) {
            throw new IllegalStatemantException("ID должны быть положительными числами");
        }


        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден: " + filmId);
        }
        if (user == null) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }


        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        Set<Long> likes = film.getLikes();


        if (likes.contains(userId)) {
            throw new IllegalStatemantException("Пользователь " + userId + " уже лайкнул фильм " + filmId);
        }


        likes.add(userId);
        filmStorage.save(film);

        log.info("Лайк добавлен: filmId={}, userId={}", filmId, userId);

        return likes;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

        if (filmId == null || userId == null) {
            throw new NotFoundException("Оба ID должны быть указаны");
        }

        try {

            Film film = filmStorage.findById(filmId);


            Set<Long> likes = Optional.ofNullable(film.getLikes())
                    .orElseGet(HashSet::new);


            if (!likes.contains(userId)) {
                throw new NotFoundException("Пользователь " + userId + " не лайкал фильм " + filmId);
            }


            likes.remove(userId);
            film.setLikes(likes);
            filmStorage.save(film);

        } catch (NotFoundException e) {
            throw new NotFoundException("Фильм или пользователь не существует");
        }
    }

    @Override
    public List<Film> getMostLikedFilms(Integer count) {
        List<Film> films = filmStorage.getFilms();
        if (films == null || films.isEmpty()) {
            return Collections.emptyList();
        }

        int limit = (count != null) ? count : 10;

        return films.stream()
                .sorted(Comparator.comparing((Film f) ->
                        f.getLikes() != null ? f.getLikes().size() : 0, Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }

}
