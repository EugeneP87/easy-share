package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Создание нового пользователя");
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id,
                                         @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с ID " + id);
        return userClient.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Удаление пользователя с ID " + id);
        userClient.delete(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Получение перечня всех пользователей");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        log.info("Получение пользователя с ID " + id);
        return userClient.getUserById(id);
    }

}