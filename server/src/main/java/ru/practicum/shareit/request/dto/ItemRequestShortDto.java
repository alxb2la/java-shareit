package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemRequestShortDto {
    private long id;
    private String description;
    private LocalDateTime created;
}
