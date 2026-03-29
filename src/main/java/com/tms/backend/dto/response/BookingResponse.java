package com.tms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long showId;
    private String movieTitle;
    private String theatreName;
    private String screenName;
    private LocalDateTime showTime;
    private List<SeatInfo> seats;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime bookedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private Long seatId;
        private String seatNumber;
        private String seatType;
        private String status;
    }
}
