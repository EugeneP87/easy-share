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
public class CommentTest {

    @Test
    public void toCommentDtoTest() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        Item item = new Item(1, "Item 1", "Description", true);
        User author = new User(1, "User", "user@user.com");
        Comment comment = new Comment(id, text, created);
        comment.setItem(item);
        comment.setAuthor(author);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertEquals(id, commentDto.getId());
        assertEquals(text, commentDto.getText());
        assertEquals(item.getId(), commentDto.getItem().getId());
        assertEquals(author.getName(), commentDto.getAuthorName());
        assertEquals(created, commentDto.getCreated());
    }

    @Test
    public void toCommentTest() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(id, text, null, "User", created);
        Comment comment = CommentMapper.toComment(commentDto);
        assertEquals(id, comment.getId());
        assertEquals(text, comment.getText());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
        assertEquals(created, comment.getCreated());
    }

    @Test
    public void toDtoListTest() {
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
        assertEquals(2, commentDtos.size());
        assertEquals(id1, commentDtos.get(0).getId());
        assertEquals(text1, commentDtos.get(0).getText());
        assertEquals(created1, commentDtos.get(0).getCreated());
        assertNotNull(commentDtos.get(0).getItem());
        assertNotNull(commentDtos.get(0).getAuthorName());
        assertEquals(id2, commentDtos.get(1).getId());
        assertEquals(text2, commentDtos.get(1).getText());
        assertEquals(created2, commentDtos.get(1).getCreated());
        assertNotNull(commentDtos.get(1).getItem());
        assertNotNull(commentDtos.get(1).getAuthorName());
    }

    @Test
    public void commentConstructorTest() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        Item item = new Item(1, "Item 1", "Description", true);
        User author = new User(1, "User", "user@user.com");
        Comment comment = new Comment(id, text, item, author, created);
        assertEquals(id, comment.getId());
        assertEquals(text, comment.getText());
        assertEquals(created, comment.getCreated());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
    }

    @Test
    public void commentConstructorWithIdAndTextTest() {
        int id = 1;
        String text = "Comment";
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(id, text, created);
        assertEquals(id, comment.getId());
        assertEquals(text, comment.getText());
        assertEquals(created, comment.getCreated());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
    }

}