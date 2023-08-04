package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создание запроса вещи пользователем с ID " + userId);
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @PathVariable(value = "id") int requestId) {
        log.info("Получение запроса вещи по ID запроса " + requestId);
        return requestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByOwnerId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение запроса вещи по ID пользователя: " + userId);
        return requestClient.getItemRequestByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Получение всех запросов вещей");
        return requestClient.findAllItemRequests(from, size, userId);
    }

}