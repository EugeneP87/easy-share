package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {

    public final Map<Integer, User> users = new HashMap<>();
    private static int userId = 1;

    private static int generateUserId() {
        return userId++;
    }

    @Override
    public User create(User user) {
        checkUserForFailAndDuplicateEmail(user);
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("Создание нового пользователя " + user);
        return user;
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
        if (users.values().stream()
                .filter(user -> user.getEmail().equals(userDto.getEmail()))
                .anyMatch(user -> !user.getId().equals(id))) {
            throw new AlreadyExistException("Ошибка: пользователь с таким Email уже зарегистрирован");
        }
        User currentUser = users.get(id);
        if (userDto.getName() != null) {
            currentUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null
                && users.values().stream().noneMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            currentUser.setEmail(userDto.getEmail());
        }
        users.put(currentUser.getId(), currentUser);
        log.info("Обновление данных пользователя " + currentUser);
        return UserMapper.toUserDto(currentUser);
    }

    @Override
    public void delete(int id) {
        log.info("Удаление пользователя " + getUserById(id));
        users.remove(id);
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Пользователь для отображения не найден");
        }
    }

    private void checkUserForFailAndDuplicateEmail(User user) {
        String message;
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            message = "Электронная почта не может быть пустой и должна содержать символ @";
            log.info(message);
            throw new IncorrectParameterException(message);
        }
        if (users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            message = "Пользователь с таким Email уже существует";
            log.info(message);
            throw new AlreadyExistException(message);
        }
    }

}