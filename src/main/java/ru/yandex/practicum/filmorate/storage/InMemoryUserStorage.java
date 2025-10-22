package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Validator.UserValidator;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long,User> userIdList = new HashMap<>();
    private Long userId = 1L;

    @Override
    public User createUser(User user) throws IllegalAccessException {
        UserValidator.validate(user);
        Long id = generateId();
        user.setId(id);
        String userName = user.getName();
        if (userName == null || userName.isEmpty()) {
            user.setName(user.getLogin());
        }

        userIdList.put(id,user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        User currentUser = userIdList.get(id);

        if (Objects.isNull(currentUser)) {
            String errorMessage = "Пользователь не найден";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        userIdList.put(id,user);

        return user;
    }

    @Override
    public List<User> getUsersList() {
        if (userIdList.isEmpty()) {
            throw new NotFoundException("Список пользователей пуст");
        } else {
            return new ArrayList<>(userIdList.values());
        }

    }

    @Override
    public User findById(Long id) {
        if (userIdList.get(id) == null) {
           throw new NotFoundException("Пользователь не найден");
        } else {
            return userIdList.get(id);
        }
    }

    public Long generateId() {
        return userId++;
    }
}
