
package com.tms.backend.service;

import com.tms.backend.dto.request.ScreenRequest;
import com.tms.backend.dto.request.TheatreRequest;
import com.tms.backend.dto.response.ScreenResponse;
import com.tms.backend.dto.response.TheatreResponse;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.model.entity.Screen;
import com.tms.backend.model.entity.Seat;
import com.tms.backend.model.entity.Theatre;
import com.tms.backend.model.enums.SeatType;
import com.tms.backend.repository.ScreenRepository;
import com.tms.backend.repository.SeatRepository;
import com.tms.backend.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    // ── Public: list all theatres, optionally filtered by city ──
    @Transactional(readOnly = true)
    public List<TheatreResponse> getAllTheatres(String city) {
        List<Theatre> theatres;
        if (city != null && !city.trim().isEmpty()) {
            theatres = theatreRepository.findByCityIgnoreCase(city.trim());
        } else {
            theatres = theatreRepository.findAll();
        }
        return theatres.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Public: get single theatre by ID ──
    @Transactional(readOnly = true)
    public TheatreResponse getTheatreById(Long id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre", "id", id));
        return toResponse(theatre);
    }

    // ── Admin: create a new theatre ──
    @Transactional
    public TheatreResponse createTheatre(TheatreRequest request) {
        Theatre theatre = Theatre.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .totalScreens(request.getTotalScreens() != null ? request.getTotalScreens() : 1)
                .build();
        return toResponse(theatreRepository.save(theatre));
    }

    // ── Admin: add a screen to a theatre (auto-generates seats) ──
    @Transactional
    public ScreenResponse createScreen(ScreenRequest request) {
        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre", "id", request.getTheatreId()));

        Screen screen = Screen.builder()
                .theatre(theatre)
                .screenName(request.getScreenName())
                .totalSeats(request.getTotalSeats())
                .build();

        Screen saved = screenRepository.save(screen);

        // Auto-generate seat entities (10 seats per row: A1-A10, B1-B10, etc.)
        int totalSeats = request.getTotalSeats();
        int seatsPerRow = 10;
        int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        List<Seat> seats = new ArrayList<>();
        int seatCount = 0;
        for (int r = 0; r < totalRows && seatCount < totalSeats; r++) {
            char rowLetter = (char) ('A' + r);
            // Seat type distribution: last 2 rows = RECLINER, 2 before that = PREMIUM, rest = STANDARD
            SeatType seatType;
            if (r >= totalRows - 2) {
                seatType = SeatType.RECLINER;
            } else if (r >= totalRows - 4) {
                seatType = SeatType.PREMIUM;
            } else {
                seatType = SeatType.STANDARD;
            }

            int seatsInThisRow = Math.min(seatsPerRow, totalSeats - seatCount);
            for (int s = 1; s <= seatsInThisRow; s++) {
                seats.add(Seat.builder()
                        .screen(saved)
                        .seatNumber(rowLetter + "" + s)
                        .seatType(seatType)
                        .isActive(true)
                        .build());
                seatCount++;
            }
        }
        seatRepository.saveAll(seats);

        return toScreenResponse(saved);
    }

    // ── Get screens for a theatre ──
    @Transactional(readOnly = true)
    public List<ScreenResponse> getScreensByTheatre(Long theatreId) {
        if (!theatreRepository.existsById(theatreId)) {
            throw new ResourceNotFoundException("Theatre", "id", theatreId);
        }
        return screenRepository.findByTheatreId(theatreId)
                .stream().map(this::toScreenResponse).collect(Collectors.toList());
    }

    // ── Entity → Response DTO mappers ──
    private TheatreResponse toResponse(Theatre theatre) {
        List<ScreenResponse> screenResponses = theatre.getScreens() != null
                ? theatre.getScreens().stream().map(this::toScreenResponse).collect(Collectors.toList())
                : Collections.emptyList();

        return TheatreResponse.builder()
                .id(theatre.getId())
                .name(theatre.getName())
                .city(theatre.getCity())
                .address(theatre.getAddress())
                .totalScreens(theatre.getTotalScreens())
                .screens(screenResponses)
                .build();
    }

    private ScreenResponse toScreenResponse(Screen screen) {
        return ScreenResponse.builder()
                .id(screen.getId())
                .theatreId(screen.getTheatre().getId())
                .theatreName(screen.getTheatre().getName())
                .screenName(screen.getScreenName())
                .totalSeats(screen.getTotalSeats())
                .build();
    }
}
