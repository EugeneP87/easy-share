package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final InMemoryUserStorage inMemoryUserStorage;

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    public UserDto update(int userId, UserDto userDto) {
        return inMemoryUserStorage.update(userId, userDto);
    }

    public void delete(int userId) {
        inMemoryUserStorage.delete(userId);
    }

    public Collection<User> findAll() {
        log.info("Получение перечня всех пользователей");
        return inMemoryUserStorage.users.values();
    }

    public User getUserById(int userId) {
        return inMemoryUserStorage.getUserById(userId);
    }

}