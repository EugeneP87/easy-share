package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс BookingDto имеет следующие поля
 * @id — уникальный идентификатор бронирования;
 * @start — дата и время начала бронирования;
 * @end — дата и время конца бронирования;
 * @item — вещь, которую пользователь бронирует;
 * @booker — пользователь, который осуществляет бронирование;
 * @status — статус бронирования. Может принимать одно из следующих
 * значений: <p> WAITING — новое бронирование, ожидает одобрения, <p> APPROVED —
 * бронирование подтверждено владельцем, <p> REJECTED — бронирование
 * отклонено владельцем, <p> CANCELED — бронирование отменено создателем
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;

}