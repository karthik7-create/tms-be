package com.tms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private long totalMovies;
    private long totalTheatres;
    private long totalShows;
    private long totalBookings;
    private long totalUsers;
    private BigDecimal totalRevenue;
}
