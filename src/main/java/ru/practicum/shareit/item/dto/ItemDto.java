package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Класс ItemDto имеет следующие поля:
 *
 * @id — уникальный идентификатор вещи;
 * @name — краткое название;
 * @description — развёрнутое описание;
 * @available — статус о том, доступна или нет вещь для аренды;
 * @request — если вещь была создана по запросу другого пользователя, то в этом
 * поле будет храниться ссылка на соответствующий запрос.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private PartialBookingDto lastBooking;
    private PartialBookingDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;

    public ItemDto(int id, String name, String description, Boolean available, int requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

}