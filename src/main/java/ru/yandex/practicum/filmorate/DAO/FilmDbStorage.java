package ru.yandex.practicum.filmorate.DAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import org.springframework.jdbc.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper = new FilmRowMapper();

    public FilmDbStorage(JdbcTemplate jbdc) {
        this.jdbc = jbdc;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = """
                INSERT INTO film (name, description, release_date, duration, rating_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa() != null ? (int) film.getMpa().getId() : 1);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateFilmGenres(film);
        return findById(film.getId());
    }


    public void addLike(long filmId, long userId) {
        jdbc.update("INSERT INTO film_like (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }


    public void deleteLike(long filmId, long userId) {
        jdbc.update("DELETE FROM film_like WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Film> getFilms() {
        String sqlFilm = """
                SELECT f.*, r.name AS rating_name
                FROM film f
                JOIN rating r ON f.rating_id = r.rating_id
                """;

        List<Film> films = jdbc.query(sqlFilm, filmRowMapper);

        if (films.isEmpty()) {
            return films;
        }

        loadGenresForFilms(films);

        return films;
    }

    @Override
    public Film save(Film film) {
        String sql = """
                UPDATE film SET name=?, description=?, release_date=?, duration=?, rating_id=?
                WHERE film_id=?
                """;

        int updated = jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : 1,
                film.getId());

        if (updated == 0)
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");

        updateFilmGenres(film);
        return findById(film.getId());
    }


    public List<Film> getMostLikedFilms(int count) {
        String sql = """
        SELECT f.*, r.name AS rating_name, COUNT(fl.user_id) AS likes_count
        FROM film f
        JOIN rating r ON f.rating_id = r.rating_id
        LEFT JOIN film_like fl ON f.film_id = fl.film_id
        GROUP BY f.film_id, r.rating_id, r.name, f.name, f.description, f.release_date, f.duration
        ORDER BY likes_count DESC
        LIMIT ?
        """;

        List<Film> films = jdbc.query(sql, filmRowMapper, count);

        if (films.isEmpty()) {
            loadGenresForFilms(films);
        }

        return films;
    }

    @Override
    public Film findById(Long id) {
        String sql = """
                SELECT f.*, r.name AS rating_name
                FROM film f
                JOIN rating r ON f.rating_id = r.rating_id
                WHERE f.film_id = ?
                """;
        Film film = jdbc.query(sql, filmRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        loadGenresAndLikes(film);
        return film;
    }

    private void loadGenresForFilms(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).toList();
        if (filmIds.isEmpty()) {
            return;
        }

        String inSql = filmIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genre fg " +
                "JOIN genre g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + inSql + ") " +
                "ORDER BY g.genre_id";

        Map<Long, LinkedHashSet<Genre>> filmGenres = new HashMap<>();

        jdbc.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            filmGenres.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        }, filmIds.toArray());

        for (Film film : films) {
            film.setGenres(filmGenres.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
    }

    private void updateFilmGenres(Film film) {
        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    private void loadGenresAndLikes(Film film) {
        String sqlGenres = """
            SELECT g.genre_id, g.name
            FROM genre g
            JOIN film_genre fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.genre_id
            """;

        List<Genre> genreList = jdbc.query(sqlGenres,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")),
                film.getId());

        film.setGenres(new LinkedHashSet<>(genreList));
    }

}
