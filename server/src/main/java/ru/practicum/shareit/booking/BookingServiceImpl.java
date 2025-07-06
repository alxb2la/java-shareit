package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Collection;
import java.util.List;

/**
 * BookingServiceImpl — класс, реализующий интерфейс BookingService.
 * Содержит всю бизнес-логику по работе с объектами Booking:
 * добавление нового Booking, обновление статуса Booking,
 * получение Booking по ID, получение списка всех Booking пользователя,
 * получение списка Booking по вещам пользователя.
 * Выполняет запросы к базе данных
 * Определяет транзакционные методы.
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingPartialDto addBooking(Long userId, BookingCreateDto bookingCreateDto) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + bookingCreateDto.getItemId()));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        if (!item.getAvailable()) {
            throw new ValidationException("Item with ID: " + bookingCreateDto.getItemId() +
                    " not available for booking");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new ValidationException("User with ID: " + userId + " is the owner of Item with ID: "
                    + bookingCreateDto.getItemId());
        }

        bookingCreateDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingCreateDto, item, booker));
        return BookingMapper.toBookingPartialDto(booking);
    }

    @Override
    @Transactional
    public BookingPartialDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found by id: " + bookingId));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("User with ID: " + userId +
                    " is not a owner of Item with ID: " + booking.getItem().getId());
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingPartialDto(updatedBooking);
    }

    @Override
    public BookingPartialDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found by id: " + bookingId));
        if ((!userId.equals(booking.getBooker().getId())) && !(userId.equals(booking.getItem().getOwner().getId()))) {
            throw new ValidationException("User with ID: " + userId +
                    " is not a owner of Item with ID: " + booking.getItem().getId() +
                    " or is not a owner of Booking with ID: " + booking.getId());
        }
        return BookingMapper.toBookingPartialDto(booking);
    }

    @Override
    public Collection<BookingPartialDto> getUserBookings(Long userId, BookingState state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedOperationException("Booking state param: " + state + " unsupported");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingPartialDto)
                .toList();
    }

    @Override
    public Collection<BookingPartialDto> getAllUserItemsBookings(Long userId, BookingState state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        List<Long> itemsId = itemRepository.findByOwnerIdOrderByIdAsc(userId).stream()
                .map(Item::getId)
                .toList();
        if (itemsId.isEmpty()) {
            return List.of();
        }

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemIdInOrderByStartDesc(itemsId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemsId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemsId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemsId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemsId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemsId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedOperationException("Booking state param: " + state + " unsupported");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingPartialDto)
                .toList();
    }
}
