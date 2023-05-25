package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Класс контроллеров User
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя");
        return userServiceImpl.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id, @Valid @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с ID " + id);
        return userServiceImpl.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Удаление пользователя с ID " + id);
        userServiceImpl.delete(id);
    }

    @GetMapping()
    public Collection<User> findAll() {
        log.info("Получение перечня всех пользователей");
        return userServiceImpl.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Получение пользователя с ID " + id);
        return userServiceImpl.getUserById(id);
    }

}