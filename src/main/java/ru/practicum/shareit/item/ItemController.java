package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

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
        return itemServiceImpl.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable int itemId,
                          @RequestHeader("X-Sharer-User-Id") int userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemServiceImpl.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        return itemServiceImpl.getItemById(itemId);
    }

    @GetMapping()
    public Collection<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemServiceImpl.findAllUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemServiceImpl.search(text);
    }

}