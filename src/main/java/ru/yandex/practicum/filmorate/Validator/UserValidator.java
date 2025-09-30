package ru.yandex.practicum.filmorate.Validator;

import ru.yandex.practicum.filmorate.annotations.BirthdayValid;
import ru.yandex.practicum.filmorate.exeptions.ValidException;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class UserValidator {

    public static void validate(Object object) throws IllegalAccessException, ValidException {

        for (Field field : object.getClass().getDeclaredFields()) {
            BirthdayValid bv = field.getAnnotation(BirthdayValid.class);
            if (bv != null) {
                field.setAccessible(true);
                Object value = field.get(object);
                if (((LocalDate) value).isAfter(LocalDate.now())) {
                    throw new ValidException("Нелья родиться в будущем :)");
                }
            }
        }
    }
}