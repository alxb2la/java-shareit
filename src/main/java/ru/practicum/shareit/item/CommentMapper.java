package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentPartialDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toComment(CommentCreateDto commentCreateDto, Item item, User author) {
        return Comment.builder()
                .id(null)
                .text(commentCreateDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentPartialDto toCommentPartialDto(Comment comment) {
        return CommentPartialDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
