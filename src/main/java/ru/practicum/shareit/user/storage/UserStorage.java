package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserStorage {

    User create(User user);

    UserDto update(int userId, UserDto userDto);

    void delete(int userId);

    User getUserById(int userId);

}