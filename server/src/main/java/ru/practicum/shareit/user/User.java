package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс User имеет следующие поля:
 *
 * @id — уникальный идентификатор пользователя;
 * @name — имя или логин пользователя;
 * @email — адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

}