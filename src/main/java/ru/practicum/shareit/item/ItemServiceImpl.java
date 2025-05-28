package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPartialDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemPartialDto addItem(Long userId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        Item item = itemRepository.create(itemMapper.toItem(itemCreateDto, owner));
        return itemMapper.toItemPartialDto(item);
    }

    @Override
    public ItemPartialDto updateItem(Long userId, ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findById(itemUpdateDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemUpdateDto.getId()));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        if (item.getOwner().getId().longValue() != userId.longValue()) {
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
        Item updatedItem = itemRepository.update(item);
        return itemMapper.toItemPartialDto(updatedItem);
    }

    @Override
    public ItemPartialDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemId));
        return itemMapper.toItemPartialDto(item);
    }

    @Override
    public Collection<ItemPartialDto> getAllItemsByOwnerId(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toItemPartialDto)
                .toList();
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
}
