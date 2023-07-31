package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;

}