package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPartialDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private final User itemOwner = User.builder()
            .id(1L)
            .name("User1Name")
            .email("User1Email@Email123.net")
            .build();

    private final User booker = User.builder()
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
            .request(null)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusMonths(1))
            .end(LocalDateTime.now().plusMonths(2))
            .status(BookingStatus.APPROVED)
            .item(item1)
            .booker(booker)
            .build();


    @Test
    void addBooking_whenAllCreateParamsIsCorrect_thenSaveAndReturnBooking() {
        Booking bookingWithStatusWaiting = Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
        BookingPartialDto templateBooking = BookingMapper.toBookingPartialDto(bookingWithStatusWaiting);
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingWithStatusWaiting);

        BookingPartialDto createdBooking = bookingService.addBooking(booker.getId(), bookingCreateDto);

        assertEquals(createdBooking.getId(), templateBooking.getId());
        assertEquals(createdBooking.getStart(), templateBooking.getStart());
        assertEquals(createdBooking.getEnd(), templateBooking.getEnd());
        assertEquals(createdBooking.getStatus(), templateBooking.getStatus());
        assertEquals(createdBooking.getItem().id(), templateBooking.getItem().id());
        assertEquals(createdBooking.getItem().name(), templateBooking.getItem().name());
        assertEquals(createdBooking.getBooker().id(), templateBooking.getBooker().id());
        assertEquals(createdBooking.getBooker().name(), templateBooking.getBooker().name());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_whenItemNotFound_thenThrowNotFoundException() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(booker.getId(), bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenBookerNotFound_thenThrowNotFoundException() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(booker.getId(), bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenItemIsNotAvailableForBooking_thenThrowValidationException() {
        Item itemFalseAvailable = Item.builder()
                .id(item1.getId())
                .name(item1.getName())
                .description(item1.getDescription())
                .available(false)
                .owner(item1.getOwner())
                .request(null)
                .build();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemFalseAvailable));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(booker.getId(), bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenBookerIsTheOwnerOfItem_thenThrowValidationException() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));

        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(itemOwner.getId(), bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }


    @Test
    void updateBookingStatus_whenApprovedParamIsTrue_thenReturnBookingWithStatusAPPROVED() {
        Booking bookingWithStatusWaiting = Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingWithStatusWaiting));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingService.updateBookingStatus(itemOwner.getId(),
                booking.getId(), true);

        Mockito.verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking updatedBooking = bookingArgumentCaptor.getValue();

        assertEquals(updatedBooking.getId(), booking.getId());
        assertEquals(updatedBooking.getStart(), booking.getStart());
        assertEquals(updatedBooking.getEnd(), booking.getEnd());
        assertEquals(updatedBooking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void updateBookingStatus_whenApprovedParamIsFalse_thenReturnBookingWithStatusREJECTED() {
        Booking bookingWithStatusWaiting = Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingWithStatusWaiting));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingService.updateBookingStatus(itemOwner.getId(),
                booking.getId(), false);

        Mockito.verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking updatedBooking = bookingArgumentCaptor.getValue();

        assertEquals(updatedBooking.getId(), booking.getId());
        assertEquals(updatedBooking.getStart(), booking.getStart());
        assertEquals(updatedBooking.getEnd(), booking.getEnd());
        assertEquals(updatedBooking.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void updateBookingStatus_whenBookingNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(itemOwner.getId(), booking.getId(), true));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));

    }

    @Test
    void updateBookingStatus_whenUserIsNotTheOwnerOfItem_thenThrowValidationException() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.updateBookingStatus(booker.getId(), booking.getId(), true));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }


    @Test
    void getBookingById_whenAllParamsIsCorrect_thenReturnBooking() {
        BookingPartialDto templateBooking = BookingMapper.toBookingPartialDto(booking);
        Mockito
                .when(bookingRepository.findById(eq(booking.getId())))
                .thenReturn(Optional.of(booking));

        BookingPartialDto getBooking = bookingService.getBookingById(booker.getId(), booking.getId());

        assertEquals(getBooking.getId(), templateBooking.getId());
        assertEquals(getBooking.getStart(), templateBooking.getStart());
        assertEquals(getBooking.getEnd(), templateBooking.getEnd());
        assertEquals(getBooking.getStatus(), templateBooking.getStatus());
        assertEquals(getBooking.getItem().id(), templateBooking.getItem().id());
        assertEquals(getBooking.getItem().name(), templateBooking.getItem().name());
        assertEquals(getBooking.getBooker().id(), templateBooking.getBooker().id());
        assertEquals(getBooking.getBooker().name(), templateBooking.getBooker().name());
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(booking.getId());
    }


    @Test
    void getBookingById_whenBookingNotFound_thenThrowNotFoundException() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booker.getId(), booking.getId()));
    }

    @Test
    void getBookingById_whenUserIsNotTheOwnerOfItemOrNotTheOwnerOfBooking_thenThrowValidationException() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(5L, booking.getId()));
    }


    @Test
    void getUserBookings_whenBookingStateIsALL_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findByBookerIdOrderByStartDesc(eq(booker.getId())))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getUserBookings(booker.getId(), BookingState.ALL));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdOrderByStartDesc(eq(booker.getId()));
    }

    @Test
    void getUserBookings_whenBookingStateIsCURRENT_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()),
                        any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getUserBookings(booker.getId(), BookingState.CURRENT));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()),
                        any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_whenBookingStateIsPAST_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(booker.getId()),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getUserBookings(booker.getId(), BookingState.PAST));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_whenBookingStateIsFUTURE_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(booker.getId()),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getUserBookings(booker.getId(), BookingState.FUTURE));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_whenBookingStateIsWAITING_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(eq(booker.getId()),
                        any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getUserBookings(booker.getId(), BookingState.WAITING));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatusOrderByStartDesc(eq(booker.getId()), any(BookingStatus.class));
    }

    @Test
    void getUserBookings_whenBookingStateIsREJECTED_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(eq(booker.getId()),
                        any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getUserBookings(booker.getId(), BookingState.REJECTED));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatusOrderByStartDesc(eq(booker.getId()), any(BookingStatus.class));
    }

    @Test
    void getUserBookings_whenBookerIsNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getUserBookings(booker.getId(), BookingState.WAITING));
    }


    @Test
    void getAllUserItemsBookings_whenBookingStateIsALL_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInOrderByStartDesc(eq(List.of(item1.getId()))))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.ALL));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInOrderByStartDesc(eq(List.of(item1.getId())));
    }

    @Test
    void getAllUserItemsBookings_whenBookingStateIsCURRENT_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                        eq(List.of(item1.getId())), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.CURRENT));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(eq(List.of(item1.getId())),
                        any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getAllUserItemsBookings_whenBookingStateIsPAST_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(eq(List.of(item1.getId())),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.PAST));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndEndBeforeOrderByStartDesc(eq(List.of(item1.getId())), any(LocalDateTime.class));
    }

    @Test
    void getAllUserItemsBookings_whenBookingStateIsFUTURE_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(eq(List.of(item1.getId())),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.FUTURE));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStartAfterOrderByStartDesc(eq(List.of(item1.getId())), any(LocalDateTime.class));
    }

    @Test
    void getAllUserItemsBookings_whenBookingStateIsWAITING_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInAndStatusOrderByStartDesc(eq(List.of(item1.getId())),
                        any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.WAITING));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStatusOrderByStartDesc(eq(List.of(item1.getId())), any(BookingStatus.class));
    }

    @Test
    void getAllUserItemsBookings_whenBookingStateIsREJECTED_thenReturnCollectionOfBookings() {
        List<BookingPartialDto> templateBookings = List.of(BookingMapper.toBookingPartialDto(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item1));
        Mockito
                .when(bookingRepository.findByItemIdInAndStatusOrderByStartDesc(eq(List.of(item1.getId())),
                        any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<BookingPartialDto> getBookings =
                new ArrayList<>(bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.REJECTED));

        assertEquals(getBookings.size(), templateBookings.size());
        assertEquals(getBookings.get(0).getId(), templateBookings.get(0).getId());
        assertEquals(getBookings.get(0).getStart(), templateBookings.get(0).getStart());
        assertEquals(getBookings.get(0).getEnd(), templateBookings.get(0).getEnd());
        assertEquals(getBookings.get(0).getStatus(), templateBookings.get(0).getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStatusOrderByStartDesc(eq(List.of(item1.getId())), any(BookingStatus.class));
    }

    @Test
    void getAllUserItemsBookings_whenBookerIsNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllUserItemsBookings(booker.getId(), BookingState.WAITING));
    }

    @Test
    void getAllUserItemsBookings_whenUserDoesNotHaveTheItems_thenReturnEmptyList() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of());

        bookingService.getAllUserItemsBookings(itemOwner.getId(), BookingState.WAITING);

        Mockito.verify(bookingRepository, Mockito.never()).findByItemIdInOrderByStartDesc(anyList());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(anyList(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemIdInAndEndBeforeOrderByStartDesc(anyList(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemIdInAndStartAfterOrderByStartDesc(anyList(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemIdInAndStatusOrderByStartDesc(anyList(), any());
    }
}
