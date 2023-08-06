package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1, "User", "user@user.com");
        userDto = new UserDto(1, "UserDto", "userDto@userDto.com");
    }

    @Test
    void testCreate() {
        when(userRepository.save(any())).thenReturn(user);
        User createdUser = userServiceImpl.create(user);
        Assertions.assertEquals(user, createdUser,
                "Ошибка при создании пользователя: объект не соответствует ожидаемому");
    }

    @Test
    void testUpdate() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        UserDto currentUser = userServiceImpl.update(1, userDto);
        Assertions.assertEquals(userDto, currentUser,
                "Ошибка при обновлении пользователя: объект не соответствует ожидаемому");
    }

    @Test
    void testUpdateWhenUserIsNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userServiceImpl.update(1, userDto),
                "Ошибка при обновлении пользователя с несуществующим ID: исключение NotFoundException не было вызвано");
        Assertions.assertEquals("Пользователь для отображения не найден", exception.getMessage(),
                "Ошибка при обновлении пользователя с несуществующим ID: текст исключения не соответствует ожидаемому");
        verify(userRepository, never()).save(user);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        User actualUser = userServiceImpl.getUserById(1);
        Assertions.assertEquals(user, actualUser,
                "Ошибка при получении пользователя по ID: объект не соответствует ожидаемому");
    }

    @Test
    void testFindAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        Collection<User> actualUsers = userServiceImpl.findAllUsers();
        Assertions.assertEquals(Collections.singletonList(user), actualUsers,
                "Ошибка при получении всех пользователей: результат не соответствует ожидаемому");
    }

    @Test
    void testDelete() {
        userServiceImpl.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }

}