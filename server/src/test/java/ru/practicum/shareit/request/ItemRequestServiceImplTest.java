package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private final User requester = User.builder()
            .id(1L)
            .name("User1Name")
            .email("User1Email@Email123.net")
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request1 description")
            .created(LocalDateTime.now().minusDays(15))
            .requester(requester)
            .build();

    private final User itemOwner = User.builder()
            .id(2L)
            .name("User2Name")
            .email("User2Email@Email123.net")
            .build();

    private final Item item1 = Item.builder()
            .id(1L)
            .name("item1Name")
            .description("item1Description")
            .available(true)
            .owner(itemOwner)
            .request(request)
            .build();


    @Test
    void addItemRequest_whenAllCreateParamsIsCorrect_thenSaveAndReturnItemRequest() {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(request.getDescription())
                .build();
        ItemRequestShortDto templateRequest = ItemRequestMapper.toItemRequestShortDto(request);
        Mockito
                .when(userRepository.findById(eq(requester.getId())))
                .thenReturn(Optional.of(requester));
        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestShortDto createdRequest = itemRequestService.addItemRequest(requester.getId(), itemRequestCreateDto);

        assertEquals(createdRequest.getId(), templateRequest.getId());
        assertEquals(createdRequest.getDescription(), templateRequest.getDescription());
        assertEquals(createdRequest.getCreated(), templateRequest.getCreated());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));
    }

    @Test
    void addItemRequest_whenRequesterNotFound_thenThrowNotFoundException() {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(request.getDescription())
                .build();
        ItemRequestShortDto templateRequest = ItemRequestMapper.toItemRequestShortDto(request);
        Mockito
                .when(userRepository.findById(eq(requester.getId())))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(requester.getId(), itemRequestCreateDto));

        Mockito.verify(itemRequestRepository, Mockito.never()).save(any(ItemRequest.class));
    }


    @Test
    void getAllItemRequestsByOwnerId_whenRequesterFound_thenReturnCollectionOfItemRequests() {
        List<ItemRequestDto> templateRequests = List.of(ItemRequestMapper.toItemRequestDto(request, List.of(item1)));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester));
        Mockito
                .when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));
        Mockito
                .when(itemRepository.findByRequestIdIn(anyList()))
                .thenReturn(List.of(item1));

        List<ItemRequestDto> getRequests =
                new ArrayList<>(itemRequestService.getAllItemRequestsByOwnerId(requester.getId()));

        assertEquals(getRequests.size(), templateRequests.size());
        assertEquals(getRequests.get(0).getId(), templateRequests.get(0).getId());
        assertEquals(getRequests.get(0).getDescription(), templateRequests.get(0).getDescription());
        assertEquals(getRequests.get(0).getCreated(), templateRequests.get(0).getCreated());
        assertEquals(getRequests.get(0).getItems().size(), templateRequests.get(0).getItems().size());
    }

    @Test
    void getAllItemRequestsByOwnerId_whenRequesterNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequestsByOwnerId(requester.getId()));
    }


    @Test
    void getAllItemRequestsOfOtherUsers_whenRequesterFound_thenReturnCollectionOfItemRequests() {
        List<ItemRequestShortDto> templateRequests = List.of(ItemRequestMapper.toItemRequestShortDto(request));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester));
        Mockito
                .when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(eq(requester.getId())))
                .thenReturn(List.of(request));

        List<ItemRequestShortDto> getRequests =
                new ArrayList<>(itemRequestService.getAllItemRequestsOfOtherUsers(requester.getId()));

        assertEquals(getRequests.size(), templateRequests.size());
        assertEquals(getRequests.get(0).getId(), templateRequests.get(0).getId());
        assertEquals(getRequests.get(0).getDescription(), templateRequests.get(0).getDescription());
        assertEquals(getRequests.get(0).getCreated(), templateRequests.get(0).getCreated());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                        .findByRequesterIdNotOrderByCreatedDesc(requester.getId());
    }

    @Test
    void getAllItemRequestsOfOtherUsers_whenRequesterNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequestsOfOtherUsers(requester.getId()));
    }


    @Test
    void getItemRequestById_whenRequesterAndRequestFound_thenReturnCollectionOfItemRequests() {
        ItemRequestDto templateRequest = ItemRequestMapper.toItemRequestDto(request, List.of(item1));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester));
        Mockito
                .when(itemRequestRepository.findById(eq(request.getId())))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.findByRequestIdIn(anyList()))
                .thenReturn(List.of(item1));

        ItemRequestDto getRequest = itemRequestService.getItemRequestById(requester.getId(), request.getId());

        assertEquals(getRequest.getId(), templateRequest.getId());
        assertEquals(getRequest.getDescription(), templateRequest.getDescription());
        assertEquals(getRequest.getCreated(), templateRequest.getCreated());
        assertEquals(getRequest.getItems().get(0).id(), templateRequest.getItems().get(0).id());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(request.getId());
    }

    @Test
    void getItemRequestById_whenRequesterNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(requester.getId(), request.getId()));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(anyLong());
    }

    @Test
    void getItemRequestById_whenRequestNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester));
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(requester.getId(), request.getId()));
    }
}
