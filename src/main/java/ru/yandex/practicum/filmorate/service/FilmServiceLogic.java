package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.DAO.UserDbStorage;
import ru.yandex.practicum.filmorate.Validator.FilmValidator;
import ru.yandex.practicum.filmorate.exeptions.IllegalStatemantException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;



@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceLogic implements FilmService {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;

    @Override
    public Film createFilm(Film film) throws IllegalAccessException {

        if (film.getMpa().getId() > 6 || film.getMpa().getId() < 0) {
            throw new NotFoundException("Такого рейтинга нет");
        }

        if (film.getGenres().stream()
                .anyMatch(genre -> genre.getId() < 0 || genre.getId() > 7)) {
            throw new NotFoundException("Такого жанра нет");
        }


        FilmValidator.validate(film);
        filmDbStorage.createFilm(film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {

        if (film == null) {
            String errorMessage = String.format("Фильм с таким ID не найден");
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        filmDbStorage.save(film);

        return film;
    }


    @Override
    public List<Film> getFilms() {
        if (filmDbStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Список фильмов пуст");
        } else {
            return filmDbStorage.getFilms();
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


        Film film = filmDbStorage.findById(filmId);
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


        filmDbStorage.addLike(filmId,userId);

        log.info("Лайк добавлен: filmId={}, userId={}", filmId, userId);

        return likes;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

        if (filmId == null || userId == null) {
            throw new NotFoundException("Оба ID должны быть указаны");
        }

        filmDbStorage.deleteLike(filmId,userId);
    }

    @Override
    public List<Film> getMostLikedFilms(Integer count) {

        return filmDbStorage.getMostLikedFilms(count);
    }

    @Override
    public Film getFilmById(long id) {
        return filmDbStorage.findById(id);
    }
}
