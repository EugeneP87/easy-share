package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestServiceImpl itemRequestServiceImpl;

    @Test
    void createItemRequestTest() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 7, 30, 12, 0, 0);
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1, "Item 1", "Description 1", true, 1),
                new ItemDto(2, "Item 2", "Description 2", false, 2)
        );
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        ItemRequestDto expectedResponse = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        when(itemRequestServiceImpl.createItemRequest(eq(1), any())).thenReturn(expectedResponse);
        String response = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(expectedResponse, mapper.readValue(response, ItemRequestDto.class));
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 7, 30, 12, 0, 0);
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1, "Item 1", "Description 1", true, 1),
                new ItemDto(2, "Item 2", "Description 2", false, 2)
        );
        ItemRequestDto expectedResponse = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        when(itemRequestServiceImpl.getItemRequestById(eq(1), eq(1))).thenReturn(expectedResponse);
        String response = mockMvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(expectedResponse, mapper.readValue(response, ItemRequestDto.class));
    }

    @Test
    void getItemRequestByOwnerIdTest() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 7, 30, 12, 0, 0);
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1, "Item 1", "Description 1", true, 1),
                new ItemDto(2, "Item 2", "Description 2", false, 2)
        );
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        List<ItemRequestDto> expectedResponse = Collections.singletonList(itemRequestDto);
        when(itemRequestServiceImpl.getItemRequestByOwnerId(eq(1))).thenReturn(expectedResponse);
        String response = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<ItemRequestDto> actualResponse = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllItemRequestsTest() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 7, 30, 12, 0, 0);
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1, "Item 1", "Description 1", true, 1),
                new ItemDto(2, "Item 2", "Description 2", false, 2)
        );
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        List<ItemRequestDto> expectedResponse = Collections.singletonList(itemRequestDto);
        when(itemRequestServiceImpl.findAllItemRequests(eq(1), anyInt(), anyInt())).thenReturn(expectedResponse);
        String response = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<ItemRequestDto> actualResponse = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getItemRequestByOwnerIdNewTest() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 7, 30, 12, 0, 0);
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1, "Item 1", "Description 1", true, 1),
                new ItemDto(2, "Item 2", "Description 2", false, 2)
        );
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        List<ItemRequestDto> expectedResponse = Collections.singletonList(itemRequestDto);
        when(itemRequestServiceImpl.getItemRequestByOwnerId(eq(1))).thenReturn(expectedResponse);
        String response = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<ItemRequestDto> actualResponse = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllItemRequestsNewTest() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 7, 30, 12, 0, 0);
        List<ItemDto> itemDtos = List.of(
                new ItemDto(1, "Item 1", "Description 1", true, 1),
                new ItemDto(2, "Item 2", "Description 2", false, 2)
        );
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", createdDateTime, itemDtos);
        List<ItemRequestDto> expectedResponse = Collections.singletonList(itemRequestDto);
        when(itemRequestServiceImpl.findAllItemRequests(eq(1), anyInt(), anyInt())).thenReturn(expectedResponse);
        String response = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<ItemRequestDto> actualResponse = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(expectedResponse, actualResponse);
    }

}