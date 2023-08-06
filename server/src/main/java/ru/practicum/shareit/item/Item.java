package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;
import java.util.List;

/**
 * Класс Item имеет следующие поля:
 *
 * @id — уникальный идентификатор вещи;
 * @name — краткое название;
 * @description — развёрнутое описание;
 * @available — статус о том, доступна или нет вещь для аренды;
 * @owner — владелец вещи;
 * @request — если вещь была создана по запросу другого пользователя, то в этом
 * поле будет храниться ссылка на соответствующий запрос.
 */

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;
    @Transient
    private PartialBookingDto lastBooking;
    @Transient
    private PartialBookingDto nextBooking;
    @Transient
    private List<CommentDto> comments;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item(int id, String name, String description, boolean available, int ownerId, ItemRequest itemRequest) {
    }

}