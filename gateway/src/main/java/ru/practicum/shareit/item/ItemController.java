package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи пользователем с ID " + userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable int itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи с ID" + itemId + " пользователем с ID " + userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @PathVariable int itemId) {
        log.info("Получение вещи с ID " + itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                                   @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(name = "size", defaultValue = "20") @Min(1) int size) {
        log.info("Получение всех вещей пользователя с ID " + userId);
        return itemClient.findAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Поиск вещи по слову " + text);
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.debug("Добавление комментария к вещи с ID" + itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

}