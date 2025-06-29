package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemRequestCreateDto {

    private String description;
}
