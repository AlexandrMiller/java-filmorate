package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Validator.UserValidator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws IllegalAccessException {
        log.info("Получен запрос на создание пользователя");
        UserValidator.validate(user);
        User createdUser = userService.createUser(user);
        log.info("Выполнен запрос на создание Пользователя");
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновления данных пользователя");
        User updatedUser = userService.updateUser(user);
        log.info("Запрос на обновления данных выполнен");
        return updatedUser;
    }


    @GetMapping
    public List<User> getUsersList() {
        log.info("Запрос на список пользователей");
        return userService.getUsersList();
    }


    @GetMapping("/{id}/friends/common/{friendid}")
    public List<User> getMutualFriends(
            @PathVariable("friendid") Long userId,
            @PathVariable("id") Long clientId) throws IllegalAccessException {
        log.info("Запрос на список общих друзей");
        return userService.mutualFriends(userId,clientId);
    }

    @PutMapping("/{id}/friends/{friendid}")
    public void addFriend(@PathVariable("friendid") Long userId, @PathVariable("id") Long clientId) {
        log.info("Запрос на добавление в друзья");
        userService.addFriend(userId,clientId);
    }

    @DeleteMapping("/{id}/friends/{friendid}")
    public void deleteFriend(@PathVariable("friendid") Long userId,@PathVariable("id") Long clientId) {
        log.info("Запрос на удаление из друзей");
        userService.deleteFriend(userId,clientId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendsOfClient(@PathVariable("id")Long id) {
      return userService.getFriendsOfClient(id);
    }
}
