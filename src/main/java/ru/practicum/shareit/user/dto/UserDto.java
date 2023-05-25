package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс UserDto имеет следующие поля:
 *
 * @id — уникальный идентификатор пользователя;
 * @name — имя или логин пользователя;
 * @email — адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
 */

@Data
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String name;
    private String email;

}