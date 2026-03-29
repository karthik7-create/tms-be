// ═══ STUB ENTITY — will be enhanced by Maha's Theatre Module ═══
package com.tms.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theatres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "total_screens")
    @Builder.Default
    private Integer totalScreens = 1;
}
