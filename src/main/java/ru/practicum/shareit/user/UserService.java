package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    User create(User user);

    UserDto update(int userId, UserDto userDto);

    void delete(int userId);

    User getUserById(int userId);

}