package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.IllegalStatemantException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceLogic implements UserService {

  private final UserStorage userStorage;

    @Override
    public User createUser(User user) throws IllegalAccessException {
      return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
       return userStorage.updateUser(user);
    }

    @Override
    public List<User> getUsersList() {
       return userStorage.getUsersList();
    }


    @Override
    public void addFriend(Long userId, Long clientId) {

        if (userId.equals(clientId)) {
            throw new ValidException("Нельзя добавить самого себя в друзья");
        }

        if (userId == null || clientId == null) {
            throw new ValidException("ID пользователя не может быть null");
        }

        if (userStorage.findById(userId) == null || userStorage.findById(clientId) == null) {
            throw new ValidException("Пользователь не найден");

        }

        User user = userStorage.findById(userId);
        User client = userStorage.findById(clientId);

        Set<Long> userFriends = Optional.ofNullable(user.getFriendsId())
                .orElseGet(HashSet::new);
        Set<Long> clientFriends = Optional.ofNullable(client.getFriendsId())
                .orElseGet(HashSet::new);


        user.setFriendsId(userFriends);
        client.setFriendsId(clientFriends);


        boolean userAdded = userFriends.add(clientId);
        boolean clientAdded = clientFriends.add(userId);


        if (userAdded) {
            userStorage.updateUser(user);
        }
        if (clientAdded) {
            userStorage.updateUser(client);
        }
    }

    @Override
    public void deleteFriend(Long userId,Long clientId) {
        if (userId.equals(clientId)) {
            throw new ValidException("Нельзя удалить самого себя");
        }

        if (userId == null || clientId == null) {
            throw new ValidException("ID пользователя не может быть null");
        }

        if (userStorage.findById(userId) == null || userStorage.findById(clientId) == null) {
            throw new ValidException("Пользователь не найден");

        }

        User user = userStorage.findById(userId);
        User client = userStorage.findById(clientId);

        Set<Long> friends = user.getFriendsId();
        Set<Long> friendsClient = client.getFriendsId();

        if (friends == null || friendsClient == null) {
            return;
        }

         if (!(friendsClient.contains(userId))) {
             throw new NotFoundException("Друг не найден");
         }

         friends.remove(clientId);
         friendsClient.remove(userId);
         userStorage.updateUser(user);
         userStorage.updateUser(client);
    }

    @Override
    public List<User> mutualFriends(Long userId,Long clientId) throws IllegalStatemantException {
        if (userId.equals(clientId)) {
            throw new ValidException("Нельзя смотреть общих друзей с собой");
        }

        if (userId == null || clientId == null) {
            throw new ValidException("ID пользователя не может быть null");
        }

        if (userStorage.findById(userId) == null || userStorage.findById(clientId) == null) {
            throw new ValidException("Пользователь не найден");
        }

        User user = userStorage.findById(userId);
        User client = userStorage.findById(clientId);

        if (user.getFriendsId() == null || client.getFriendsId() == null) {
            throw new IllegalStatemantException("Список не инициализирован");
        }

        Set<Long> usersFriends = user.getFriendsId();
        Set<Long> clientsFriends = client.getFriendsId();

        List<Long> mutualFriends = usersFriends.stream()
                .filter(clientsFriends::contains)
                .collect(Collectors.toList());

        List<User> allUsers = userStorage.getUsersList();

        return allUsers.stream()
                .filter(u -> mutualFriends.contains(u.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getFriendsOfClient(Long id) {
       User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (user.getFriendsId() == null) {
            return Collections.emptyList();
        }

        Set<Long> friendIds = user.getFriendsId();
        Collection<User> friends = userStorage.getUsersList();
        
        return friends.stream()
                .filter(u -> friendIds.contains(u.getId()))
                .collect(Collectors.toList());
    }
}
