package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentTest {

    @Test
    void testToCommentDto() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        Item item = new Item(1, "Item 1", "Description", true);
        User author = new User(1, "User", "user@user.com");
        Comment comment = new Comment(id, text, created);
        comment.setItem(item);
        comment.setAuthor(author);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertEquals(id, commentDto.getId(), "Ошибка при сравнении ID комментария");
        assertEquals(text, commentDto.getText(), "Ошибка при сравнении текста комментария");
        assertEquals(item.getId(), commentDto.getItem().getId(), "Ошибка при сравнении ID предмета");
        assertEquals(author.getName(), commentDto.getAuthorName(), "Ошибка при сравнении имени автора комментария");
        assertEquals(created, commentDto.getCreated(), "Ошибка при сравнении даты создания комментария");
    }

    @Test
    void testToComment() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(id, text, null, "User", created);
        Comment comment = CommentMapper.toComment(commentDto);
        assertEquals(id, comment.getId(), "Ошибка при сравнении ID комментария");
        assertEquals(text, comment.getText(), "Ошибка при сравнении текста комментария");
        assertNull(comment.getItem(), "Ошибка: предмет должен быть null");
        assertNull(comment.getAuthor(), "Ошибка: автор комментария должен быть null");
        assertEquals(created, comment.getCreated(), "Ошибка при сравнении даты создания комментария");
    }

    @Test
    void testToDtoList() {
        int id1 = 1;
        String text1 = "Comment 1";
        LocalDateTime created1 = LocalDateTime.now();
        int id2 = 2;
        String text2 = "Comment 2";
        LocalDateTime created2 = LocalDateTime.now();
        Item item1 = new Item(1, "Item 1", "Description", true);
        Item item2 = new Item(2, "Item 2", "Description", true);
        User author1 = new User(1, "User", "user@user.com");
        User author2 = new User(2, "User", "user@user.com");
        Comment comment1 = new Comment(id1, text1, item1, author1, created1);
        Comment comment2 = new Comment(id2, text2, item2, author2, created2);
        List<Comment> comments = Arrays.asList(comment1, comment2);
        List<CommentDto> commentDtos = CommentMapper.toDtoList(comments);
        assertEquals(2, commentDtos.size(), "Неправильное количество комментариев в списке");
        assertEquals(id1, commentDtos.get(0).getId(), "Ошибка при сравнении ID комментария 1");
        assertEquals(text1, commentDtos.get(0).getText(), "Ошибка при сравнении текста комментария 1");
        assertEquals(created1, commentDtos.get(0).getCreated(), "Ошибка при сравнении даты создания комментария 1");
        assertNotNull(commentDtos.get(0).getItem(), "Предмет комментария 1 не должен быть null");
        assertNotNull(commentDtos.get(0).getAuthorName(), "Автор комментария 1 не должен быть null");
        assertEquals(id2, commentDtos.get(1).getId(), "Ошибка при сравнении ID комментария 2");
        assertEquals(text2, commentDtos.get(1).getText(), "Ошибка при сравнении текста комментария 2");
        assertEquals(created2, commentDtos.get(1).getCreated(), "Ошибка при сравнении даты создания комментария 2");
        assertNotNull(commentDtos.get(1).getItem(), "Предмет комментария 2 не должен быть null");
        assertNotNull(commentDtos.get(1).getAuthorName(), "Автор комментария 2 не должен быть null");
    }

    @Test
    void testCommentConstructor() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        Item item = new Item(1, "Item 1", "Description", true);
        User author = new User(1, "User", "user@user.com");
        Comment comment = new Comment(id, text, item, author, created);
        assertEquals(id, comment.getId(), "Ошибка при сравнении ID комментария");
        assertEquals(text, comment.getText(), "Ошибка при сравнении текста комментария");
        assertEquals(created, comment.getCreated(), "Ошибка при сравнении даты создания комментария");
        assertEquals(item, comment.getItem(), "Ошибка при сравнении предмета комментария");
        assertEquals(author, comment.getAuthor(), "Ошибка при сравнении автора комментария");
    }

    @Test
    void testCommentConstructorWithIdAndText() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(id, text, created);
        assertEquals(id, comment.getId(), "Ошибка при сравнении ID комментария");
        assertEquals(text, comment.getText(), "Ошибка при сравнении текста комментария");
        assertEquals(created, comment.getCreated(), "Ошибка при сравнении даты создания комментария");
        assertNull(comment.getItem(), "Ошибка: предмет должен быть null");
        assertNull(comment.getAuthor(), "Ошибка: автор комментария должен быть null");
    }

}