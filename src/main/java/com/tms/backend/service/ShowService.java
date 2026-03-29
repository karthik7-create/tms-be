// ═══ FILE: src/main/java/com/tms/backend/service/ShowService.java ═══
package com.tms.backend.service;

import com.tms.backend.dto.request.ShowRequest;
import com.tms.backend.dto.response.ShowResponse;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.model.entity.Movie;
import com.tms.backend.model.entity.Screen;
import com.tms.backend.model.entity.Show;
import com.tms.backend.model.enums.ShowStatus;
import com.tms.backend.repository.MovieRepository;
import com.tms.backend.repository.ScreenRepository;
import com.tms.backend.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;

    // ── Public: filter shows by movieId, city, date (all optional) ──
    @Transactional(readOnly = true)
    public List<ShowResponse> getShows(Long movieId, String city, LocalDate date) {
        return showRepository.findShowsWithFilters(movieId, city, date)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Public: get single show with full details ──
    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long id) {
        Show show = showRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));
        return toResponse(show);
    }

    // ── Admin: create a new show ──
    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", request.getMovieId()));

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen", "id", request.getScreenId()));

        if (!movie.getIsActive()) {
            throw new IllegalArgumentException("Cannot create show for an inactive movie");
        }

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .showTime(request.getShowTime())
                .price(request.getPrice())
                .status(ShowStatus.SCHEDULED)
                .build();

        Show saved = showRepository.save(show);

        // Re-fetch with JOIN FETCH to populate all associations for the response
        return toResponse(showRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    // ── Admin: update an existing show ──
    @Transactional
    public ShowResponse updateShow(Long id, ShowRequest request) {
        Show show = showRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));

        // Allow changing movie and screen
        if (!show.getMovie().getId().equals(request.getMovieId())) {
            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", request.getMovieId()));
            show.setMovie(movie);
        }
        if (!show.getScreen().getId().equals(request.getScreenId())) {
            Screen screen = screenRepository.findById(request.getScreenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Screen", "id", request.getScreenId()));
            show.setScreen(screen);
        }

        show.setShowTime(request.getShowTime());
        show.setPrice(request.getPrice());

        Show saved = showRepository.save(show);
        return toResponse(showRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    // ── Entity → Response DTO mapper ──
    private ShowResponse toResponse(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .moviePosterUrl(show.getMovie().getPosterUrl())
                .screenId(show.getScreen().getId())
                .screenName(show.getScreen().getScreenName())
                .theatreId(show.getScreen().getTheatre().getId())
                .theatreName(show.getScreen().getTheatre().getName())
                .theatreCity(show.getScreen().getTheatre().getCity())
                .showTime(show.getShowTime())
                .price(show.getPrice())
                .status(show.getStatus())
                .build();
    }
}
