package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Validator.UserValidator;
import ru.yandex.practicum.filmorate.exeptions.ValidException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long,User> userIdList = new HashMap<>();
    private Long userId = 1L;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws IllegalAccessException {
        log.info("Получен запрос на получение пользователя");
        UserValidator.validate(user);
        Long id = generateId();
        user.setId(id);
        String userName = user.getName();
        if (userName == null || userName.isEmpty()) {
            user.setName(user.getLogin());
        }

        userIdList.put(id,user);
        log.info("Выполнен запрос на создание Пользователя");

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Long id = user.getId();
        User currentUser = userIdList.get(id);

        if (Objects.isNull(currentUser)) {
            String errorMessage = "Пользователь не найден";
            log.error(errorMessage);
            throw new ValidException(errorMessage);
        }

        userIdList.put(id,user);

        return user;
    }

    public Long generateId() {
        return userId++;
    }

    @GetMapping
    public List<User> getUsersList() {
        return new ArrayList<>(userIdList.values());
    }
}
