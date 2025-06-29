package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestShortDto addItemRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        ItemRequest itemRequest =
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestCreateDto, requester));
        return ItemRequestMapper.toItemRequestShortDto(itemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequestsByOwnerId(Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        return requests.stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, items))
                .toList();
    }

    @Override
    public Collection<ItemRequestShortDto> getAllItemRequestsOfOtherUsers(Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestShortDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found by id: " + requestId));
        List<Item> items = itemRepository.findByRequestIdIn(List.of(requestId));
        return ItemRequestMapper.toItemRequestDto(request, items);
    }
}
