package com.tms.backend.service;

import com.tms.backend.dto.request.BookingRequest;
import com.tms.backend.dto.response.BookingResponse;
import com.tms.backend.exception.BadRequestException;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.exception.SeatAlreadyBookedException;
import com.tms.backend.model.entity.*;
import com.tms.backend.model.enums.BookingSeatStatus;
import com.tms.backend.model.enums.BookingStatus;
import com.tms.backend.model.enums.ShowStatus;
import com.tms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findByIdWithDetails(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", request.getShowId()));

        if (show.getStatus() != ShowStatus.SCHEDULED) {
            throw new BadRequestException("Show is not available for booking (status: " + show.getStatus() + ")");
        }

        // Validate seats exist and are active
        List<Seat> seats = seatRepository.findAllActiveByIds(request.getSeatIds());
        if (seats.size() != request.getSeatIds().size()) {
            throw new BadRequestException("One or more selected seats are invalid or inactive");
        }

        // Check for double-booking — seats already locked/confirmed for this show
        List<BookingSeat> conflicting = bookingSeatRepository
                .findLockedOrConfirmedSeats(show.getId(), request.getSeatIds());
        if (!conflicting.isEmpty()) {
            String conflictingSeatNumbers = conflicting.stream()
                    .map(bs -> bs.getSeat().getSeatNumber())
                    .collect(Collectors.joining(", "));
            throw new SeatAlreadyBookedException(
                    "Seats already booked: " + conflictingSeatNumbers);
        }

        // Calculate total (price per seat × number of seats)
        BigDecimal totalAmount = show.getPrice().multiply(BigDecimal.valueOf(seats.size()));

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .build();
        bookingRepository.save(booking);

        // Lock seats
        List<BookingSeat> bookingSeats = seats.stream()
                .map(seat -> BookingSeat.builder()
                        .booking(booking)
                        .seat(seat)
                        .status(BookingSeatStatus.LOCKED)
                        .build())
                .collect(Collectors.toList());
        bookingSeatRepository.saveAll(bookingSeats);
        booking.setBookingSeats(bookingSeats);

        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse confirmBooking(Long bookingId, String userEmail) {
        Booking booking = getBookingForUser(bookingId, userEmail);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking cannot be confirmed (status: " + booking.getStatus() + ")");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.getBookingSeats().forEach(bs -> bs.setStatus(BookingSeatStatus.CONFIRMED));

        bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        Booking booking = getBookingForUser(bookingId, userEmail);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getBookingSeats().forEach(bs -> bs.setStatus(BookingSeatStatus.RELEASED));

        bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserIdWithDetails(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllWithDetails()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Booking getBookingForUser(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("This booking does not belong to you");
        }
        return booking;
    }

    private BookingResponse mapToResponse(Booking booking) {
        Show show = booking.getShow();
        Screen screen = show.getScreen();
        Theatre theatre = screen.getTheatre();

        List<BookingResponse.SeatInfo> seatInfos = booking.getBookingSeats().stream()
                .map(bs -> BookingResponse.SeatInfo.builder()
                        .seatId(bs.getSeat().getId())
                        .seatNumber(bs.getSeat().getSeatNumber())
                        .seatType(bs.getSeat().getSeatType().name())
                        .status(bs.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .showId(show.getId())
                .movieTitle(show.getMovie().getTitle())
                .theatreName(theatre.getName())
                .screenName(screen.getScreenName())
                .showTime(show.getShowTime())
                .seats(seatInfos)
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}
