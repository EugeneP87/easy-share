package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс ItemRequest имеет следующие поля:
 *
 * @id — уникальный идентификатор запроса;
 * @description — текст запроса, содержащий описание требуемой вещи;
 * @requestor — пользователь, создавший запрос;
 * @created — дата и время создания запроса.
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne()
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;
    @Column(name = "created_time")
    private LocalDateTime created;

    public ItemRequest(int id, String description, User requestor) {
    }

}