package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private BookingRepository bookingRepository;

    @Test
    public void findBookingById() {
        int userId = 1;
        int bookingId = 100;
        User booker = new User();
        booker.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Booking result = BookingMapper.toBooking(bookingService.findBookingById(userId, bookingId));
        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(booking, result);
    }

    @Test
    public void findBookingById_NotFound() {
        int userId = 1;
        int bookingId = 100;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(userId, bookingId));
    }

    @Test
    public void findBookingById_NotOwner() {
        int userId = 1;
        int bookingId = 100;
        User booker = new User();
        booker.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertDoesNotThrow(() -> bookingService.findBookingById(userId, bookingId));
    }

}