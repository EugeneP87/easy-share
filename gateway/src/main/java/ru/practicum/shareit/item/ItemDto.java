package ru.practicum.shareit.item;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    private int id;
    @NotBlank()
    private String name;
    @NotBlank()
    private String description;
    @NotNull()
    private Boolean available;
    private Integer requestId;

}