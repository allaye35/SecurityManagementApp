package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "geolocalisation_gps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GeolocalisationGPS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float precision;

    @Embedded // Intègre directement latitude et longitude dans la table
    private GeoPoint position;

    @OneToMany(mappedBy = "geolocalisationGPS", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions;
}
