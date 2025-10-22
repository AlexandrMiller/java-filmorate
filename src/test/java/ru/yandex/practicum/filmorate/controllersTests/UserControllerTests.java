package ru.yandex.practicum.filmorate.controllersTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.ValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTests {

    private InMemoryUserStorage userStorage = new InMemoryUserStorage();

    @Test
    void birthdayShouldBeReal() {
        LocalDate date = LocalDate.of(3000,1,1);
        User user = User.builder().birthday(date).login("login").email("a@mail.ru").build();
        ValidException valid = assertThrows(ValidException.class,() -> userStorage.createUser(user));
        assertEquals("Нелья родиться в будущем :)",valid.getMessage());
    }
}
