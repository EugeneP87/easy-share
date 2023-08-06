package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс ItemRequestDto имеет следующие поля:
 *
 * @id — уникальный идентификатор запроса;
 * @description — текст запроса, содержащий описание требуемой вещи;
 * @requestor — пользователь, создавший запрос;
 * @created — дата и время создания запроса.
 */

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequestDto {

    private int id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;

}