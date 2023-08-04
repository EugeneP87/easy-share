package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Класс UserDto имеет следующие поля:
 *
 * @id — уникальный идентификатор пользователя;
 * @name — имя или логин пользователя;
 * @email — адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
 */

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private int id;
    private String name;
    private String email;

}