// ═══ FILE: src/main/java/com/tms/backend/service/SeatService.java ═══
package com.tms.backend.service;

import com.tms.backend.dto.request.BulkSeatRequest;
import com.tms.backend.dto.response.SeatResponse;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.model.entity.Screen;
import com.tms.backend.model.entity.Seat;
import com.tms.backend.model.entity.Show;
import com.tms.backend.repository.ScreenRepository;
import com.tms.backend.repository.SeatRepository;
import com.tms.backend.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;

    /**
     * GET /api/shows/{id}/seats — Seat layout + availability for a specific show.
     * Returns all active seats for the show's screen.
     * NOTE: Real booking-based availability (checking booking_seats) will be
     *       integrated when the Booking Module is implemented by Karthik.
     *       Currently returns all active seats as "available".
     */
    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsForShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", showId));

        List<Seat> seats = seatRepository.findActiveSeatsForScreen(show.getScreen().getId());

        // TODO: When Booking Module is ready, query booking_seats to check
        //       which seats are LOCKED/CONFIRMED for this show and mark them unavailable
        return seats.stream().map(seat -> toResponse(seat, true)).collect(Collectors.toList());
    }

    /**
     * POST /api/admin/screens/{id}/seats — Bulk create seats for a screen.
     * Creates all seats in a single transaction.
     */
    @Transactional
    public List<SeatResponse> bulkCreateSeats(Long screenId, BulkSeatRequest request) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen", "id", screenId));

        List<Seat> seats = request.getSeats().stream()
                .map(entry -> Seat.builder()
                        .screen(screen)
                        .seatNumber(entry.getSeatNumber())
                        .seatType(entry.getSeatType())
                        .isActive(true)
                        .build())
                .collect(Collectors.toList());

        List<Seat> savedSeats = seatRepository.saveAll(seats);

        // Update the screen's total seat count
        screen.setTotalSeats(seatRepository.findByScreenIdAndIsActiveTrue(screenId).size());
        screenRepository.save(screen);

        return savedSeats.stream()
                .map(seat -> toResponse(seat, true))
                .collect(Collectors.toList());
    }

    // ── Entity → Response DTO mapper ──
    private SeatResponse toResponse(Seat seat, boolean isAvailable) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .isActive(seat.getIsActive())
                .isAvailable(isAvailable)
                .build();
    }
}
