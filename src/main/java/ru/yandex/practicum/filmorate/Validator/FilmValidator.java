package ru.yandex.practicum.filmorate.Validator;

import ru.yandex.practicum.filmorate.annotations.DateAndDurationValid;
import ru.yandex.practicum.filmorate.exeptions.ValidException;

import java.lang.reflect.Field;
import java.time.LocalDate;


public class FilmValidator {

    public static void validate(Object object) throws ValidException, IllegalAccessException {
        LocalDate minReleaseDate = LocalDate.of(1895,12,28);
        for (Field field : object.getClass().getDeclaredFields()) {
            DateAndDurationValid rd = field.getAnnotation(DateAndDurationValid.class);
            if (rd != null) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value instanceof LocalDate) {
                    if (((LocalDate) value).isBefore(minReleaseDate)) {
                        throw new ValidException("Дата релиза не может быть раньше 28.12.1895");
                    }
                }
                if (value instanceof Integer) {
                    if (((Integer) value) <= 0) {
                        throw new ValidException("Продолжительность фильма должна быть больше нуля");
                    }
                }
            }
        }
    }
}
