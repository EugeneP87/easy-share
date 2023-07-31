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
public class UserServiceImplTest {

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
    void create() {
        when(userRepository.save(any())).thenReturn(user);
        User createdUser = userServiceImpl.create(user);
        Assertions.assertEquals(user, createdUser);
    }

    @Test
    void update() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        UserDto currentUser = userServiceImpl.update(1, userDto);
        Assertions.assertEquals(userDto, currentUser);
    }

    @Test
    void updateWhenUserIsNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assertions.assertEquals("Пользователь для отображения не найден",
                Assertions.assertThrows(NotFoundException.class,
                        () -> userServiceImpl.update(1, userDto)).getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        User actualUser = userServiceImpl.getUserById(1);
        Assertions.assertEquals(user, actualUser);
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        Collection<User> actualUsers = userServiceImpl.findAllUsers();
        Assertions.assertEquals(Collections.singletonList(user), actualUsers);
    }

    @Test
    void delete() {
        userServiceImpl.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }

}