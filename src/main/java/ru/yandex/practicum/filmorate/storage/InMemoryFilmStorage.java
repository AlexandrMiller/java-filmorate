package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Validator.FilmValidator;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {


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
        if (!filmById.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }
        return filmById.put(film.getId(),film);
    }

    @Override
    public Film findById(Long id) {
        if (filmById.get(id) == null) {
           throw new NotFoundException("Фильм не найден");
        }
        return filmById.get(id);
    }

    public Long generateId() {
        return filmId++;
    }
}
