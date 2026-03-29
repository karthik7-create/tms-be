// ═══ STUB ENTITY — will be enhanced by Maha's Seat Module ═══
package com.tms.backend.model.entity;

import com.tms.backend.model.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    @Builder.Default
    private SeatType seatType = SeatType.STANDARD;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
