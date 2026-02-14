package com.logistics.delivery.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "geographic_zones")
public class GeographicZone {

    @Id
    private String id;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
