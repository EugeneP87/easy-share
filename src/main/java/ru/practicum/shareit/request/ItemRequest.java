package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс ItemRequest имеет следующие поля:
 *
 * @id — уникальный идентификатор запроса;
 * @description — текст запроса, содержащий описание требуемой вещи;
 * @requestor — пользователь, создавший запрос;
 * @created — дата и время создания запроса.
 */
@Data
@AllArgsConstructor
public class ItemRequest {

    private int id;
    private String description;
    private int requestor;
    private LocalDateTime created;

}