// ═══ STUB ENTITY — will be enhanced by Maha's Theatre Module ═══
package com.tms.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "screens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @Column(name = "screen_name", length = 100)
    private String screenName;

    @Column(name = "total_seats")
    private Integer totalSeats;
}
