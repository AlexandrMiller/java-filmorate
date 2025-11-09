package ru.yandex.practicum.filmorate.DAO;

import ru.yandex.practicum.filmorate.model.Mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper = new MpaRowMapper();

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM rating ORDER BY rating_id";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String sql = "SELECT * FROM rating WHERE rating_id = ?";
        List<Mpa> result = jdbcTemplate.query(sql, mpaRowMapper, id);
        return result.stream().findFirst();
    }


}
