// ═══ FILE: src/main/java/com/tms/backend/model/entity/Seat.java ═══
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screen screen;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
