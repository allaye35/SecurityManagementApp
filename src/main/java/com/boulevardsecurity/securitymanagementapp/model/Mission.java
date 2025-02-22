package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.StatutMission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "missions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_mission")
    private StatutMission statutMission;


    @ManyToMany
    @JoinTable(
            name = "mission_agents",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    @Builder.Default
    private Set<AgentDeSecurite> agents = new HashSet<>();



    // Relation avec Planning (Une mission appartient à un seul planning)
    @ManyToOne
    @JoinColumn(name = "planning_id", nullable = false)
    @ToString.Exclude
    private Planning planning;

    // 🔹 Relation avec Site (Une mission se déroule dans un seul site)
    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    @ToString.Exclude
    private Site site;

    @ManyToOne
    @JoinColumn(name = "geolocalisation_id") // Clé étrangère vers GeolocalisationGPS
    private GeolocalisationGPS geolocalisationGPS;

    //  Relation avec RapportIntervention
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RapportIntervention> rapports;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false) // Clé étrangère vers Entreprise
    private Entreprise entreprise;

}