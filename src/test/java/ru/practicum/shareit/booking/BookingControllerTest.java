package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingServiceImpl bookingServiceImpl;

    @Test
    void createBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        PartialBookingDto partialBookingDto = new PartialBookingDto();
        partialBookingDto.setStart(LocalDateTime.now().plusDays(1));
        partialBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingServiceImpl.create(anyInt(), any(PartialBookingDto.class))).thenReturn(bookingDto);
        String response = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(partialBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(bookingDto, mapper.readValue(response, BookingDto.class));
    }

    @Test
    void updateBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        when(bookingServiceImpl.updateBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);
        String response = mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(bookingDto, mapper.readValue(response, BookingDto.class));
    }

    @Test
    void findAllBookingsByUserIdTest() throws Exception {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        when(bookingServiceImpl.findAllBookingsByUserId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingDtoList);
        String response = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<BookingDto> actualBookingList = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(bookingDtoList, actualBookingList);
    }

    @Test
    void findAllBookingsByOwnerIdTest() throws Exception {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        when(bookingServiceImpl.findAllBookingsByOwnerId(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(bookingDtoList);
        String response = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        List<BookingDto> actualBookingList = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(bookingDtoList, actualBookingList);
    }

    @Test
    void findBookingByIdTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        when(bookingServiceImpl.findBookingById(anyInt(), anyInt())).thenReturn(bookingDto);
        String response = mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(Charset.defaultCharset());
        assertEquals(bookingDto, mapper.readValue(response, BookingDto.class));
    }

}