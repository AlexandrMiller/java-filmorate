package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.DAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.DAO.GenreDbStorage;
import ru.yandex.practicum.filmorate.DAO.MpaDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Import({FilmDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    void testAddAndGetFilm() {
        Mpa mpa = mpaDbStorage.getById(1).orElseThrow();
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        Film savedFilm = filmStorage.createFilm(film);

        Film fetchedFilm = filmStorage.findById(savedFilm.getId());
        assertThat(fetchedFilm.getName()).isEqualTo("Test Film");
        assertThat(fetchedFilm.getMpa().getId()).isEqualTo(mpa.getId());
        assertThat(fetchedFilm.getGenres()).isNotNull();
    }

    @Test
    void testUpdateFilm() {
        Mpa mpa = mpaDbStorage.getById(1).orElseThrow();
        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Original description");
        film.setReleaseDate(LocalDate.of(2019, 1, 1));
        film.setDuration(90);
        film.setMpa(mpa);

        Film savedFilm = filmStorage.createFilm(film);
        savedFilm.setName("Updated Film");
        savedFilm.setDuration(100);
        filmStorage.save(savedFilm);

        Film updatedFilm = filmStorage.findById(savedFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDuration()).isEqualTo(100);
        assertThat(updatedFilm.getGenres()).isNotNull();
    }
}
