package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Класс контроллеров ItemRequest.
 */

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestServiceImpl itemRequestServiceImpl;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создание запроса вещи пользователем с ID " + userId);
        return itemRequestServiceImpl.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("{id}")
    public ItemRequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable(value = "id") int requestId) {
        log.info("Получение запроса вещи по ID запроса " + requestId);
        return itemRequestServiceImpl.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestByOwnerId(
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение запроса вещи по ID пользователя: " + userId);
        return itemRequestServiceImpl.getItemRequestByOwnerId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Получение всех запросов вещей");
        return itemRequestServiceImpl.findAllItemRequests(userId, from, size);
    }

}