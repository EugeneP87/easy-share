package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.IncorrectParameterException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IncorrectParameterException("Unknown state: " + stateParam));
        log.info("Получить бронирования с параметрами state= {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @Validated
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @RequestBody @Valid BookingDto requestDto) {
        log.info("Создание бронирования {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int bookingId) {
        log.info("Получить бронирование {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @PathVariable int bookingId,
            @RequestParam boolean approved) {
        log.info("Получить запрос на подтверждение бронирования с параметрами. approved={}, ownerId={}, bookingId={}",
                approved,
                ownerId,
                bookingId);
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IncorrectParameterException("Unknown state: " + stateParam));
        log.info("Получить бронирование по собственнику {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }

}