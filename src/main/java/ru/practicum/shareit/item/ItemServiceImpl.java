package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemMapper itemMapper, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public ItemPartialDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        Item item = itemRepository.save(itemMapper.toItem(itemCreateDto, owner));
        return itemMapper.toItemPartialDto(item);
    }

    @Override
    @Transactional
    public ItemPartialDto updateItem(Long userId, ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findById(itemUpdateDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemUpdateDto.getId()));
        if (!(item.getOwner().getId().equals(userId))) {
            throw new NotFoundException("Item's owner not found by id: " + userId);
        }

        if ((itemUpdateDto.getName() != null) && !(itemUpdateDto.getName().isBlank())) {
            item.setName(itemUpdateDto.getName());
        }
        if ((itemUpdateDto.getDescription() != null) && !(itemUpdateDto.getDescription().isBlank())) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toItemPartialDto(updatedItem);
    }

    @Override
    public ItemInfoDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemId));

        List<CommentPartialDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentPartialDto)
                .toList();
        if (userId.equals(item.getOwner().getId())) {
            List<Booking> bookings = bookingRepository.findByItemId(itemId);
            return itemMapper.toItemInfoDto(item, bookings, comments);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
            return itemMapper.toItemInfoDto(item, List.of(), comments);
        }
    }

    @Override
    public Collection<ItemInfoDto> getAllItemsByOwnerId(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        List<Booking> bookings = bookingRepository.findByItemIdInOrderByItemIdAsc(itemIds);
        List<Comment> comments = commentRepository.findByItemIdInOrderByItemIdAsc(itemIds);
        Collection<ItemInfoDto> itemInfoDtos = new ArrayList<>();

        for (Item item : items) {
            List<Booking> itemBookings = bookings.stream()
                    .filter((b) -> b.getItem().getId().equals(item.getId()))
                    .toList();
            List<CommentPartialDto> itemComments = comments.stream()
                    .filter((c) -> c.getItem().getId().equals(item.getId()))
                    .map(commentMapper::toCommentPartialDto)
                    .toList();
            itemInfoDtos.add(itemMapper.toItemInfoDto(item, itemBookings, itemComments));
        }
        return itemInfoDtos;
    }

    @Override
    public Collection<ItemPartialDto> getItemsByTextQuery(String textQuery) {
        if (textQuery.isBlank()) {
            return List.of();
        }
        return itemRepository.findByTextQuery(textQuery).stream()
                .map(itemMapper::toItemPartialDto)
                .toList();
    }

    @Override
    public CommentPartialDto addComment(Long itemId, Long authorId, CommentCreateDto commentCreateDto) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(itemId, authorId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("User with ID: " + authorId +
                        " did not rent Item with ID: " + itemId));
        Comment comment = commentRepository.save(commentMapper.toComment(commentCreateDto,
                booking.getItem(), booking.getBooker()));
        return commentMapper.toCommentPartialDto(comment);
    }
}
