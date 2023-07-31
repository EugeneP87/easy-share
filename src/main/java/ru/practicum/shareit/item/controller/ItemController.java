package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи пользователем с ID " + userId);
        return itemServiceImpl.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId,
                          @PathVariable int itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи с ID" + itemId + " пользователем с ID " + userId);
        return itemServiceImpl.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable int itemId) {
        log.debug("Удаление вещи с ID " + itemId);
        itemServiceImpl.deleteById(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") int userId,
                               @PathVariable int itemId) {
        log.info("Получение вещи с ID " + itemId);
        return itemServiceImpl.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Получение всех вещей пользователем с ID " + userId);
        return itemServiceImpl.findAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Поиск вещи по слову " + text);
        return itemServiceImpl.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.debug("Добавление комментария к вещи с ID" + itemId);
        return itemServiceImpl.addComment(itemId, userId, commentDto);
    }

}