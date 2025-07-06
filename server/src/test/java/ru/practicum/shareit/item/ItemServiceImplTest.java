package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user1 = User.builder()
            .id(1L)
            .name("User1Name")
            .email("User1Email@Email123.net")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("User2Name")
            .email("User2Email@Email123.net")
            .build();

    private final ItemRequest req1 = ItemRequest.builder()
            .id(1L)
            .description("req1_qwerwerqweeryrty")
            .created(LocalDateTime.now().minusMonths(1))
            .requester(user2)
            .build();

    private final Item item1 = Item.builder()
            .id(1L)
            .name("item1Name")
            .description("item1Description")
            .available(true)
            .owner(user1)
            .request(req1)
            .build();


    @Test
    void addItem_whenAllCreateParamsIsCorrect_thenSaveAndReturnItem() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .requestId(req1.getId())
                .build();
        ItemPartialDto templateItem = ItemMapper.toItemPartialDto(item1);
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(itemRequestRepository.findById(eq(item1.getRequest().getId())))
                .thenReturn(Optional.of(req1));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        ItemPartialDto createdItem = itemService.addItem(user1.getId(), itemCreateDto);

        assertEquals(createdItem.getId(), templateItem.getId());
        assertEquals(createdItem.getName(), templateItem.getName());
        assertEquals(createdItem.getDescription(), templateItem.getDescription());
        assertEquals(createdItem.isAvailable(), templateItem.isAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void addItem_whenItemRequestIdIsNotPresent_thenSaveAndReturnItem() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .requestId(null)
                .build();
        ItemPartialDto templateItem = ItemMapper.toItemPartialDto(item1);
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        ItemPartialDto createdItem = itemService.addItem(user1.getId(), itemCreateDto);

        assertEquals(createdItem.getId(), templateItem.getId());
        assertEquals(createdItem.getName(), templateItem.getName());
        assertEquals(createdItem.getDescription(), templateItem.getDescription());
        assertEquals(createdItem.isAvailable(), templateItem.isAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(anyLong());
    }

    @Test
    void addItem_whenUserNotFound_thenThrowNotFoundException() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .requestId(req1.getId())
                .build();
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addItem(user1.getId(), itemCreateDto));

        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(anyLong());
    }

    @Test
    void addItem_whenRequestIfPresentNotFound_thenThrowNotFoundException() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .requestId(req1.getId())
                .build();
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(itemRequestRepository.findById(eq(item1.getRequest().getId())))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addItem(user1.getId(), itemCreateDto));

        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
    }


    @Test
    void updateItem_whenUpdateParamsIsCorrect_thenUpdateAndReturnUpdatedItem() {
        ItemUpdateDto templateItem = ItemUpdateDto.builder()
                .id(item1.getId())
                .name("UpdatedItem1Name")
                .description("UpdatedItem1Description")
                .available(false)
                .build();
        Mockito
                .when(itemRepository.findById(eq(templateItem.getId())))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        itemService.updateItem(user1.getId(), templateItem);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();

        assertEquals(updatedItem.getId(), templateItem.getId());
        assertEquals(updatedItem.getName(), templateItem.getName());
        assertEquals(updatedItem.getDescription(), templateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), templateItem.getAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_whenOnlyNameParamToUpdate_thenUpdateAndReturnUpdatedItem() {
        ItemUpdateDto templateItem = ItemUpdateDto.builder()
                .id(item1.getId())
                .name("UpdatedItem1Name")
                .description(null)
                .available(null)
                .build();
        Mockito
                .when(itemRepository.findById(eq(templateItem.getId())))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        itemService.updateItem(user1.getId(), templateItem);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();

        assertEquals(updatedItem.getId(), templateItem.getId());
        assertEquals(updatedItem.getName(), templateItem.getName());
        assertEquals(updatedItem.getDescription(), item1.getDescription());
        assertEquals(updatedItem.getAvailable(), item1.getAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_whenOnlyDescriptionParamToUpdate_thenUpdateAndReturnUpdatedItem() {
        ItemUpdateDto templateItem = ItemUpdateDto.builder()
                .id(item1.getId())
                .name(null)
                .description("UpdatedItem1Description")
                .available(null)
                .build();
        Mockito
                .when(itemRepository.findById(eq(templateItem.getId())))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        itemService.updateItem(user1.getId(), templateItem);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();

        assertEquals(updatedItem.getId(), templateItem.getId());
        assertEquals(updatedItem.getName(), item1.getName());
        assertEquals(updatedItem.getDescription(), templateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), item1.getAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_whenOnlyAvailableParamToUpdate_thenUpdateAndReturnUpdatedItem() {
        ItemUpdateDto templateItem = ItemUpdateDto.builder()
                .id(item1.getId())
                .name(null)
                .description(null)
                .available(false)
                .build();
        Mockito
                .when(itemRepository.findById(eq(templateItem.getId())))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        itemService.updateItem(user1.getId(), templateItem);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();

        assertEquals(updatedItem.getId(), templateItem.getId());
        assertEquals(updatedItem.getName(), item1.getName());
        assertEquals(updatedItem.getDescription(), item1.getDescription());
        assertEquals(updatedItem.getAvailable(), templateItem.getAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_whenItemNotFound_thenThrowNotFoundException() {
        ItemUpdateDto templateItem = ItemUpdateDto.builder()
                .id(item1.getId())
                .name("UpdatedItem1Name")
                .description("UpdatedItem1Description")
                .available(false)
                .build();
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user1.getId(), templateItem));

        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
    }

    @Test
    void updateItem_whenOwnerNotFound_thenThrowNotFoundException() {
        ItemUpdateDto templateItem = ItemUpdateDto.builder()
                .id(item1.getId())
                .name("UpdatedItem1Name")
                .description("UpdatedItem1Description")
                .available(false)
                .build();
        Item itemWithOtherOwner = Item.builder()
                .id(1L)
                .name("item1Name")
                .description("item1Description")
                .available(true)
                .owner(user2)
                .request(null)
                .build();

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemWithOtherOwner));

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user1.getId(), templateItem));

        Mockito.verify(itemRepository, Mockito.never()).save(any(Item.class));
    }


    @Test
    void getItemById_whenParamsIsCorrectAndUserIsTheOwnerOfItem_thenReturnItemWithBookings() {
        User user3 = User.builder()
                .id(3L)
                .name("User3Name")
                .email("User3Email@Email123.net")
                .build();
        Booking bookingLast = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMonths(3))
                .end(LocalDateTime.now().minusMonths(2))
                .status(BookingStatus.APPROVED)
                .item(item1)
                .booker(user3)
                .build();
        Booking bookingNext = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMonths(2))
                .end(LocalDateTime.now().plusMonths(3))
                .status(BookingStatus.APPROVED)
                .item(item1)
                .booker(user3)
                .build();
        ItemInfoDto templateItem = ItemMapper.toItemInfoDto(item1, List.of(bookingLast, bookingNext), List.of());

        Mockito
                .when(itemRepository.findById(eq(item1.getId())))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(commentRepository.findByItemId(eq(item1.getId())))
                .thenReturn(List.of());
        Mockito
                .when(bookingRepository.findByItemId(eq(item1.getId())))
                .thenReturn(List.of(bookingNext, bookingLast));

        ItemInfoDto getItem = itemService.getItemById(user1.getId(), item1.getId());

        assertEquals(getItem.getId(), templateItem.getId());
        assertEquals(getItem.getName(), templateItem.getName());
        assertEquals(getItem.getDescription(), templateItem.getDescription());
        assertEquals(getItem.isAvailable(), templateItem.isAvailable());
        assertEquals(getItem.getComments(), templateItem.getComments());
        assertEquals(getItem.getLastBooking(), templateItem.getLastBooking());
        assertEquals(getItem.getNextBooking(), templateItem.getNextBooking());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(item1.getId());
    }

    @Test
    void getItemById_whenParamsIsCorrectAndUserIsNotTheOwnerOfItem_thenReturnItemWithBookingsNull() {
        Item itemWithOtherOwner = Item.builder()
                .id(3L)
                .name("item3Name")
                .description("item3Description")
                .available(true)
                .owner(user2)
                .request(null)
                .build();
        ItemInfoDto templateItem = ItemMapper.toItemInfoDto(itemWithOtherOwner, List.of(), List.of());

        Mockito
                .when(itemRepository.findById(eq(itemWithOtherOwner.getId())))
                .thenReturn(Optional.of(itemWithOtherOwner));
        Mockito
                .when(commentRepository.findByItemId(eq(itemWithOtherOwner.getId())))
                .thenReturn(List.of());
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.of(user1));

        ItemInfoDto getItem = itemService.getItemById(user1.getId(), itemWithOtherOwner.getId());

        assertEquals(getItem.getId(), templateItem.getId());
        assertEquals(getItem.getName(), templateItem.getName());
        assertEquals(getItem.getDescription(), templateItem.getDescription());
        assertEquals(getItem.isAvailable(), templateItem.isAvailable());
        assertEquals(getItem.getComments(), templateItem.getComments());
        assertEquals(getItem.getLastBooking(), templateItem.getLastBooking());
        assertEquals(getItem.getNextBooking(), templateItem.getNextBooking());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemWithOtherOwner.getId());
    }

    @Test
    void getItemById_whenItemNotFound_thenThrowNotFoundException() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(user1.getId(), item1.getId()));
    }

    @Test
    void getItemById_whenUserIsNotTheOwnerOfItemAndUserNotFound_thenThrowNotFoundException() {
        Item itemWithOtherOwner = Item.builder()
                .id(3L)
                .name("item3Name")
                .description("item3Description")
                .available(true)
                .owner(user2)
                .request(null)
                .build();
        ItemInfoDto templateItem = ItemMapper.toItemInfoDto(itemWithOtherOwner, List.of(), List.of());

        Mockito
                .when(itemRepository.findById(eq(itemWithOtherOwner.getId())))
                .thenReturn(Optional.of(itemWithOtherOwner));
        Mockito
                .when(commentRepository.findByItemId(eq(itemWithOtherOwner.getId())))
                .thenReturn(List.of());
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(user1.getId(), itemWithOtherOwner.getId()));
    }


    @Test
    void getAllItemsByOwnerId_whenUserFound_thenReturnCollectionOfItems() {
        User user3 = User.builder()
                .id(3L)
                .name("User3Name")
                .email("User3Email@Email123.net")
                .build();
        Booking bookingLast = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMonths(3))
                .end(LocalDateTime.now().minusMonths(2))
                .status(BookingStatus.APPROVED)
                .item(item1)
                .booker(user3)
                .build();
        Booking bookingNext = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMonths(2))
                .end(LocalDateTime.now().plusMonths(3))
                .status(BookingStatus.APPROVED)
                .item(item1)
                .booker(user3)
                .build();
        List<ItemInfoDto> templateItems = List.of(
                ItemMapper.toItemInfoDto(item1, List.of(bookingLast, bookingNext), List.of())
        );
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(eq(user1.getId())))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInOrderByItemIdAsc(eq(List.of(item1.getId()))))
                .thenReturn(List.of(bookingLast, bookingNext));
        Mockito
                .when(commentRepository.findByItemIdInOrderByItemIdAsc(eq(List.of(item1.getId()))))
                .thenReturn(List.of());

        List<ItemInfoDto> getItems = new ArrayList<>(itemService.getAllItemsByOwnerId(user1.getId()));

        assertEquals(getItems.size(), templateItems.size());
        assertEquals(getItems.get(0).getId(), templateItems.get(0).getId());
        assertEquals(getItems.get(0).getName(), templateItems.get(0).getName());
        assertEquals(getItems.get(0).getDescription(), templateItems.get(0).getDescription());
        assertEquals(getItems.get(0).isAvailable(), templateItems.get(0).isAvailable());
        assertEquals(getItems.get(0).getComments(), templateItems.get(0).getComments());
        assertEquals(getItems.get(0).getLastBooking(), templateItems.get(0).getLastBooking());
        assertEquals(getItems.get(0).getNextBooking(), templateItems.get(0).getNextBooking());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findByOwnerIdOrderByIdAsc(user1.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInOrderByItemIdAsc(List.of(item1.getId()));
        Mockito.verify(commentRepository, Mockito.times(1))
                .findByItemIdInOrderByItemIdAsc(List.of(item1.getId()));
    }

    @Test
    void getAllItemsByOwnerId_whenUserNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getAllItemsByOwnerId(user1.getId()));
    }


    @Test
    void getItemsByTextQuery_whenTextQueryIsCorrect_thenReturnCollectionOfItems() {
        final String textQuery = "item";
        List<ItemPartialDto> templateItems = List.of(ItemMapper.toItemPartialDto(item1));
        Mockito
                .when(itemRepository.findByTextQuery(eq(textQuery)))
                .thenReturn(List.of(item1));

        List<ItemPartialDto> getItems = new ArrayList<>(itemService.getItemsByTextQuery(textQuery));

        assertEquals(getItems.size(), templateItems.size());
        assertEquals(getItems.get(0).getId(), templateItems.get(0).getId());
        assertEquals(getItems.get(0).getName(), templateItems.get(0).getName());
        assertEquals(getItems.get(0).getDescription(), templateItems.get(0).getDescription());
        assertEquals(getItems.get(0).isAvailable(), templateItems.get(0).isAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).findByTextQuery(textQuery);
    }

    @Test
    void getItemsByTextQuery_whenTextQueryIsBlank_thenReturnEmptyCollection() {
        final String textQuery = "    ";

        List<ItemPartialDto> getItems = new ArrayList<>(itemService.getItemsByTextQuery(textQuery));

        assertTrue(getItems.isEmpty());
        Mockito.verify(itemRepository, Mockito.never()).findByTextQuery(textQuery);
    }


    @Test
    void addComment_whenAllCreateParamsIsCorrect_thenSaveAndReturnComment() {
        User user3 = User.builder()
                .id(3L)
                .name("User3Name")
                .email("User3Email@Email123.net")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("fdsdfsdfsd rwerwerwe 34234 sdxcvxcv")
                .created(LocalDateTime.now().minusMonths(1))
                .item(item1)
                .author(user3)
                .build();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text(comment.getText())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMonths(3))
                .end(LocalDateTime.now().minusMonths(2))
                .status(BookingStatus.APPROVED)
                .item(item1)
                .booker(user3)
                .build();
        CommentPartialDto templateComment = CommentMapper.toCommentPartialDto(comment);

        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentPartialDto saveComment = itemService.addComment(item1.getId(), user3.getId(), commentCreateDto);

        assertEquals(saveComment.getId(), templateComment.getId());
        assertEquals(saveComment.getText(), templateComment.getText());
        assertEquals(saveComment.getCreated(), templateComment.getCreated());
        assertEquals(saveComment.getAuthorName(), templateComment.getAuthorName());
        Mockito.verify(commentRepository, Mockito.times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_whenUserDidNotRentItem_thenThrowValidationException() {
        User user3 = User.builder()
                .id(3L)
                .name("User3Name")
                .email("User3Email@Email123.net")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("fdsdfsdfsd rwerwerwe 34234 sdxcvxcv")
                .created(LocalDateTime.now().minusMonths(1))
                .item(item1)
                .author(user3)
                .build();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text(comment.getText())
                .build();
        Mockito
                .when(bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.addComment(item1.getId(), user3.getId(), commentCreateDto));

        Mockito.verify(commentRepository, Mockito.never()).save(any(Comment.class));
    }
}
