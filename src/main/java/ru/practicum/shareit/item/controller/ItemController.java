package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Класс контроллеров Item.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;

    @PostMapping()
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи пользователем с ID " + userId);
        return itemServiceImpl.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable int itemId,
                          @RequestHeader("X-Sharer-User-Id") int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи с ID" + itemId + " пользователем с ID " + userId);
        return itemServiceImpl.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        log.info("Получение вещи с ID " + itemId);
        return itemServiceImpl.getItemById(itemId);
    }

    @GetMapping()
    public Collection<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение всех вещей пользователем с ID " + userId);
        return itemServiceImpl.findAllUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("Поиск вещи по слову " + text);
        return itemServiceImpl.search(text);
    }

}