package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemServiceImpl itemServiceImpl;

    @Test
    void testCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        when(itemServiceImpl.create(anyInt(), any(ItemDto.class))).thenReturn(itemDto);
        String response = mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(itemDto, mapper.readValue(response, ItemDto.class), "Ошибка при создании предмета");
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        when(itemServiceImpl.update(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(itemDto);
        String response = mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(itemDto, mapper.readValue(response, ItemDto.class), "Ошибка при обновлении предмета");
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemServiceImpl, times(1)).deleteById(1);
    }

    @Test
    void testGetItemById() throws Exception {
        ItemDto itemDto = new ItemDto();
        when(itemServiceImpl.getItemById(anyInt(), anyInt())).thenReturn(itemDto);
        String response = mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(itemDto, mapper.readValue(response, ItemDto.class), "Ошибка при получении предмета по ID");
    }

    @Test
    void testFindAllUserItems() throws Exception {
        ItemDto itemDto = new ItemDto();
        when(itemServiceImpl.findAllUserItems(anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDto));
        String response = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        Collection<ItemDto> actualItems = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(Collections.singletonList(itemDto), actualItems, "Ошибка при поиске всех предметов пользователя");
    }

    @Test
    void testSearchItems() throws Exception {
        ItemDto itemDto = new ItemDto();
        when(itemServiceImpl.search(anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemDto));
        String response = mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        Collection<ItemDto> actualItems = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(Collections.singletonList(itemDto), actualItems, "Ошибка при поиске предметов по тексту");
    }

    @Test
    void testAddCommentToItem() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");
        when(itemServiceImpl.addComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);
        String response = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(commentDto, mapper.readValue(response, CommentDto.class), "Ошибка при добавлении комментария к предмету");
    }

}