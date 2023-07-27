package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(User user) {
        log.info("Создание нового пользователя" + user);
        return userRepository.save(user);
    }

    @Transactional
    public UserDto update(int id, UserDto userDto) {
        User currentUser = getUserById(id);
        if (userDto.getName() != null) {
            currentUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            currentUser.setEmail(userDto.getEmail());
        }
        log.info("Обновление данных пользователя " + currentUser);
        return UserMapper.toUserDto(currentUser);
    }

    @Transactional
    public void delete(int id) {
        log.info("Удаление пользователя " + getUserById(id));
        userRepository.deleteById(id);
    }

    @Transactional
    public Collection<User> findAllUsers() {
        log.info("Получение перечня всех пользователей");
        return new ArrayList<>(userRepository.findAll());
    }

    @Transactional
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь для отображения не найден"));
    }

}