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
    void create() throws Exception {
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
        assertEquals(user, mapper.readValue(response, User.class));
    }

    @Test
    void createUserWrongEmail() throws Exception {
        User incorrectEmail = new User(1, "User", "useruser.com");
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(incorrectEmail))
                        .characterEncoding(Charset.defaultCharset())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(userServiceImpl, never()).create(user);
    }

    @Test
    void update() throws Exception {
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
        assertEquals(userDto, mapper.readValue(response, UserDto.class));
    }

    @Test
    void deleteUserById() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userServiceImpl, times(1)).delete(1);
    }

    @Test
    void findAllUsers() throws Exception {
        when(userServiceImpl.findAllUsers()).thenReturn(List.of(user));

        String response = mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<User> actualUsers = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(List.of(user), actualUsers);
    }

    @Test
    void getUserById() throws Exception {
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
        assertEquals(user, mapper.readValue(response, User.class));
    }

}