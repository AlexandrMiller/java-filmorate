package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

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
    public void addFriend(Long userId,Long clientId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
        User client = userStorage.findById(clientId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        Set<Long> friendsList = user.getFriendsId();
        Set<Long> clientFriendsList = client.getFriendsId();
        if (friendsList == null) {
            friendsList = new HashSet<>();
            user.setFriendsId(friendsList);
        }

        if (clientFriendsList == null) {
            clientFriendsList = new HashSet<>();
            client.setFriendsId(clientFriendsList);
        }

        if (clientFriendsList.add(userId)) {
            userStorage.updateUser(client);
        }

        if (friendsList.add(clientId)) {
            userStorage.updateUser(user);
        }
    }

    @Override
    public void deleteFriend(Long userId,Long clientId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
        User client = userStorage.findById(clientId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        Set<Long> friends = user.getFriendsId();
        Set<Long> friendsClient = client.getFriendsId();
         if (!(friendsClient.contains(userId))) {
             throw new NotFoundException("Друг не найден");
         }

         friends.remove(clientId);
         friendsClient.remove(userId);
    }

    @Override
    public List<Optional<User>> mutualFriends(Long userId,Long clientId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
        User client = userStorage.findById(clientId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        Set<Long> usersFriends = user.getFriendsId();
        Set<Long> clientsFriends = client.getFriendsId();

        List<Optional<User>> mutualFriends = new ArrayList<>();
        for (Long usersId : usersFriends) {
           if (client.getFriendsId().contains(usersId)) {
               mutualFriends.add(userStorage.findById(usersId));
           }
        }
        return mutualFriends;
    }
}
