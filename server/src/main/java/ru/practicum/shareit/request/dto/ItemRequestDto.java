package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;

    public record ItemShortDto(long id, String name, long ownerId) {
    }
}
