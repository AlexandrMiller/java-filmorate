package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Validator.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class inMemoryFilmStorage implements FilmStorage{

    private Map<Long,Film> filmById = new HashMap<>();
    private Long filmId = 1L;


    @Override
    public Film createFilm(Film film) throws IllegalAccessException {
        Long id = generateId();
        film.setId(id);
        FilmValidator.validate(film);
        filmById.put(id,film);

        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(filmById.values());
    }

    @Override
    public Film save(Film film) {
        filmById.put(film.getId(),film);
        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        Film film = filmById.get(id);
        return Optional.ofNullable(film);
    }

    public Long generateId() {
        return filmId++;
    }
}
