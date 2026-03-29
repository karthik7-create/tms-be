// ═══ FILE: src/main/java/com/tms/backend/service/TheatreService.java ═══
package com.tms.backend.service;

import com.tms.backend.dto.request.ScreenRequest;
import com.tms.backend.dto.request.TheatreRequest;
import com.tms.backend.dto.response.ScreenResponse;
import com.tms.backend.dto.response.TheatreResponse;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.model.entity.Screen;
import com.tms.backend.model.entity.Theatre;
import com.tms.backend.repository.ScreenRepository;
import com.tms.backend.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;

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

    // ── Admin: add a screen to a theatre ──
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
