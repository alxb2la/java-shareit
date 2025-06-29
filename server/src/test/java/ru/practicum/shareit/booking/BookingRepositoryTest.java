package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    private Long bookerId;
    private Long itemId1;
    private Long itemId2;
    private Long itemId3;
    private Long bookingId1;
    private Long bookingId2;
    private Long bookingId3;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        User itemOwner = User.builder()
                .id(null)
                .name("OwnerName")
                .email("OwnerEmail@Email123.net")
                .build();
        itemOwner = userRepository.save(itemOwner);

        User booker = User.builder()
                .id(null)
                .name("BookerName")
                .email("BookerEmail@Email123.net")
                .build();
        booker = userRepository.save(booker);
        bookerId = booker.getId();

        Item item1 = Item.builder()
                .id(null)
                .name("item1Name")
                .description("item1Description")
                .available(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item1 = itemRepository.save(item1);
        itemId1 = item1.getId();

        Item item2 = Item.builder()
                .id(null)
                .name("item2Name")
                .description("item2Description")
                .available(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item2 = itemRepository.save(item2);
        itemId2 = item2.getId();

        Item item3 = Item.builder()
                .id(null)
                .name("item3Name")
                .description("item3Description")
                .available(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item3 = itemRepository.save(item3);
        itemId3 = item3.getId();

        Booking booking = Booking.builder()
                .id(null)
                .start(LocalDateTime.of(2025, Month.MAY, 10, 12, 0))
                .end(LocalDateTime.of(2025, Month.SEPTEMBER, 10, 12, 0))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item1)
                .build();
        booking = bookingRepository.save(booking);
        bookingId1 = booking.getId();

        booking = Booking.builder()
                .id(null)
                .start(LocalDateTime.of(2025, Month.MAY, 15, 12, 0))
                .end(LocalDateTime.of(2025, Month.SEPTEMBER, 15, 12, 0))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item2)
                .build();
        booking = bookingRepository.save(booking);
        bookingId2 = booking.getId();

        booking = Booking.builder()
                .id(null)
                .start(LocalDateTime.of(2025, Month.MAY, 20, 12, 0))
                .end(LocalDateTime.of(2025, Month.SEPTEMBER, 20, 12, 0))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item3)
                .build();
        booking = bookingRepository.save(booking);
        bookingId3 = booking.getId();
    }

    @Test
    void findByBookerIdOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByBookerIdOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(100L);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                LocalDateTime.of(2025, Month.JUNE, 1, 12, 0),
                LocalDateTime.of(2025, Month.SEPTEMBER, 1, 12, 0));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0),
                LocalDateTime.of(2025, Month.SEPTEMBER, 1, 12, 0));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId,
                LocalDateTime.of(2025, Month.OCTOBER, 1, 12, 0));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId,
                LocalDateTime.of(2025, Month.AUGUST, 1, 12, 0));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId,
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId,
                LocalDateTime.of(2025, Month.JUNE, 1, 12, 0));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId,
                BookingStatus.APPROVED);

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId,
                BookingStatus.CANCELED);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemIdInOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemIdInOrderByStartDesc(List.of(itemId1, itemId2, itemId3));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByItemIdInOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemIdInOrderByStartDesc(List.of(10L, 11L, 12L));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3),
                LocalDateTime.of(2025, Month.JUNE, 1, 12, 0),
                LocalDateTime.of(2025, Month.SEPTEMBER, 1, 12, 0));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3),
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0),
                LocalDateTime.of(2025, Month.SEPTEMBER, 1, 12, 0));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemIdInAndEndBeforeOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3),
                LocalDateTime.of(2025, Month.OCTOBER, 1, 12, 0));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByItemIdInAndEndBeforeOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3),
                LocalDateTime.of(2025, Month.AUGUST, 1, 12, 0));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemIdInAndStartAfterOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3),
                LocalDateTime.of(2025, Month.APRIL, 1, 12, 0));

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByItemIdInAndStartAfterOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3),
                LocalDateTime.of(2025, Month.JUNE, 1, 12, 0));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemIdInAndStatusOrderByStartDesc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3), BookingStatus.APPROVED);

        assertEquals(3, bookings.size());
        assertEquals(bookingId3, bookings.get(0).getId());
        assertEquals(bookingId2, bookings.get(1).getId());
        assertEquals(bookingId1, bookings.get(2).getId());
    }

    @Test
    void findByItemIdInAndStatusOrderByStartDesc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(
                List.of(itemId1, itemId2, itemId3), BookingStatus.REJECTED);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findFirstByItemIdAndBookerIdAndEndBefore_whenFound_thenReturnBooking() {
        Optional<Booking> optBooking = bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(
                itemId2, bookerId, LocalDateTime.of(2025, Month.OCTOBER, 1, 12, 0));

        assertTrue(optBooking.isPresent());
        assertEquals(itemId2, optBooking.get().getItem().getId());
        assertEquals(bookerId, optBooking.get().getBooker().getId());
    }

    @Test
    void findFirstByItemIdAndBookerIdAndEndBefore_whenNotFound_thenReturnEmptyOptional() {
        Optional<Booking> optBooking = bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(
                itemId2, bookerId, LocalDateTime.of(2025, Month.AUGUST, 1, 12, 0));

        assertTrue(optBooking.isEmpty());
    }

    @Test
    void findByItemId_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemId(itemId3);

        assertEquals(1, bookings.size());
        assertEquals(itemId3, bookings.get(0).getItem().getId());
    }

    @Test
    void findByItemId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemId(10L);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemIdInOrderByItemIdAsc_whenFound_thenReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.findByItemIdInOrderByItemIdAsc(List.of(itemId1, itemId2, itemId3));

        assertEquals(3, bookings.size());
        assertEquals(itemId1, bookings.get(0).getItem().getId());
        assertEquals(itemId2, bookings.get(1).getItem().getId());
        assertEquals(itemId3, bookings.get(2).getItem().getId());
    }

    @Test
    void findByItemIdInOrderByItemIdAsc_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItemIdInOrderByItemIdAsc(List.of(5L, 6L, 7L));

        assertTrue(bookings.isEmpty());
    }


    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }
}