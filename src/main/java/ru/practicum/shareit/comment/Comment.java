package ru.practicum.shareit.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne()
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime created;

    public Comment(int id, String text, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.created = created;
    }

}