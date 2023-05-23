package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

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
        return userServiceImpl.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id, @Valid @RequestBody UserDto userDto) {
        return userServiceImpl.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userServiceImpl.delete(id);
    }

    @GetMapping()
    public Collection<User> findAll() {
        return userServiceImpl.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userServiceImpl.getUserById(id);
    }

}