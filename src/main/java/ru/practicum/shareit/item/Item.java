package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

/**
 * Класс Item имеет следующие поля:
 * @id — уникальный идентификатор вещи;
 * @name — краткое название;
 * @description — развёрнутое описание;
 * @available — статус о том, доступна или нет вещь для аренды;
 * @owner — владелец вещи;
 * @request — если вещь была создана по запросу другого пользователя, то в этом
 * поле будет храниться ссылка на соответствующий запрос.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private ItemRequest request;

    public Item(Integer id, String name, String description, Boolean available, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}