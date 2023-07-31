package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Класс контроллеров Booking.
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingServiceImpl bookingServiceImpl;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                             @Valid @RequestBody PartialBookingDto partialBookingDto) {
        return bookingServiceImpl.create(userId, partialBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @PathVariable int bookingId,
                                    @RequestParam Boolean approved) {
        return bookingServiceImpl.updateBooking(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingServiceImpl.findAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingServiceImpl.findAllBookingsByOwnerId(state, userId, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @PathVariable int bookingId) {
        return bookingServiceImpl.findBookingById(userId, bookingId);
    }

}