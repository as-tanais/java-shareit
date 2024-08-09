package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer request;

    public Item(int id, String name, String description, boolean available, Integer request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
