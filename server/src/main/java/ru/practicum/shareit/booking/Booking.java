package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс Booking имеет следующие поля
 *
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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne()
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

}