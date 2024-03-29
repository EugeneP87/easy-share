package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.CommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    private ItemClient(RestTemplate rest) {
        super(rest);
    }

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );

    }

    public ResponseEntity<Object> create(int userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }


    public ResponseEntity<Object> update(int userId, int itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(int itemId, int ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> findAllUserItems(int userId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> search(String text, int userId, int from, int size) {
        Map<String, Object> parameters = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(int userId, int itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}