package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DAO.UserDbStorage;
import ru.yandex.practicum.filmorate.exeptions.IllegalStatemantException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceLogic implements UserService {

    private final UserDbStorage userDbStorage;

    @Override
    public User createUser(User user) throws IllegalAccessException {
      return userDbStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
       return userDbStorage.updateUser(user);
    }

    @Override
    public List<User> getUsersList() {
       return userDbStorage.getUsersList();
    }


    @Override
    public void addFriend(Long userId, Long clientId) {

        if (userId.equals(clientId)) {
            throw new ValidException("Нельзя добавить самого себя в друзья");
        }

        if (userId == null || clientId == null) {
            throw new ValidException("ID пользователя не может быть null");
        }

        if (userDbStorage.findById(userId) == null || userDbStorage.findById(clientId) == null) {
            throw new NotFoundException("Пользователь не найден");

        }

        userDbStorage.addFriend(clientId,userId);


    }

    @Override
    public void deleteFriend(Long userId,Long clientId) {
        if (userId.equals(clientId)) {
            throw new ValidException("Нельзя удалить самого себя");
        }

        if (userId == null || clientId == null) {
            throw new ValidException("ID пользователя не может быть null");
        }

        if (userDbStorage.findById(userId) == null || userDbStorage.findById(clientId) == null) {
            throw new NotFoundException("Пользователь не найден");

        }

        userDbStorage.removeFriend(clientId,userId);
    }

    @Override
    public List<User> mutualFriends(Long userId,Long clientId) throws IllegalStatemantException {
        if (userId.equals(clientId)) {
            throw new ValidException("Нельзя смотреть общих друзей с собой");
        }

        if (userId == null || clientId == null) {
            throw new ValidException("ID пользователя не может быть null");
        }

        if (userDbStorage.findById(userId) == null || userDbStorage.findById(clientId) == null) {
            throw new ValidException("Пользователь не найден");
        }

        User user = userDbStorage.findById(userId);
        User client = userDbStorage.findById(clientId);

        if (user.getFriendsId() == null || client.getFriendsId() == null) {
            throw new IllegalStatemantException("Список не инициализирован");
        }

        Set<Long> usersFriends = user.getFriendsId();
        Set<Long> clientsFriends = client.getFriendsId();

        List<Long> mutualFriends = usersFriends.stream()
                .filter(clientsFriends::contains)
                .collect(Collectors.toList());

        List<User> allUsers = userDbStorage.getUsersList();

        return allUsers.stream()
                .filter(u -> mutualFriends.contains(u.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getFriendsOfClient(Long id) {
       User user = userDbStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (user.getFriendsId() == null) {
            return Collections.emptyList();
        }

        Set<Long> friendIds = user.getFriendsId();
        Collection<User> friends = userDbStorage.getUsersList();
        return friends.stream()
                .filter(u -> friendIds.contains(u.getId()))
                .collect(Collectors.toList());
    }
}
