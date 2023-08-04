package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserServiceImpl userServiceImpl;
    private final User user = new User(1, "User", "user@user.com");
    private final UserDto userDto = new UserDto(1, "UserDto", "userDto@userDto.com");

    @Test
    void testCreate() throws Exception {
        when(userServiceImpl.create(any())).thenReturn(user);
        String response = mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(user, mapper.readValue(response, User.class), "Ошибка при создании пользователя: объект не соответствует ожидаемому");
    }

    @Test
    void testUpdate() throws Exception {
        when(userServiceImpl.update(anyInt(), any())).thenReturn(userDto);
        String response = mockMvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(userDto, mapper.readValue(response, UserDto.class), "Ошибка при обновлении пользователя: объект не соответствует ожидаемому");
    }

    @Test
    void testDeleteUserById() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userServiceImpl, times(1)).delete(1);
    }

    @Test
    void testFindAllUsers() throws Exception {
        when(userServiceImpl.findAllUsers()).thenReturn(List.of(user));

        String response = mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<User> actualUsers = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(List.of(user), actualUsers, "Ошибка при получении всех пользователей: результат не соответствует ожидаемому");
    }

    @Test
    void testGetUserById() throws Exception {
        when(userServiceImpl.getUserById(anyInt())).thenReturn(user);
        String response = mockMvc.perform(get("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(user, mapper.readValue(response, User.class), "Ошибка при получении пользователя по ID: объект не соответствует ожидаемому");
    }

}