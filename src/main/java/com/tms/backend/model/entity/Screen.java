// ═══ FILE: src/main/java/com/tms/backend/model/entity/Screen.java ═══
package com.tms.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Theatre theatre;

    @Column(name = "screen_name", length = 100)
    private String screenName;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Seat> seats = new ArrayList<>();
}
