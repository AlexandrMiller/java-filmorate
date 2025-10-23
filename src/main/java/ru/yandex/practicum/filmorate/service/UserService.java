package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;


public interface UserService {

    User createUser(User user) throws IllegalAccessException;

    User updateUser(User user);

    List<User> getUsersList();

    void addFriend(Long userId,Long clientId);

    void deleteFriend(Long userId,Long clientId);

    List<User> mutualFriends(Long id, Long userId) throws IllegalAccessException;

    Collection<User> getFriendsOfClient(Long id);

}
