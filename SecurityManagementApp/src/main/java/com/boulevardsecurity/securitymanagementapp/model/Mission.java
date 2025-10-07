package com.boulevardsecurity.securitymanagementapp.model;
import com.boulevardsecurity.securitymanagementapp.Enums.StatutMission;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeMission;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "missions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    private String titre;

    private String description;

    @Column(nullable = true)
    private LocalDate dateDebut;

    @Column(nullable = true)
    private LocalDate dateFin;

    @Column(nullable = true)
    private LocalTime heureDebut;

    @Column(nullable = true)
    private LocalTime heureFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_mission")
    private StatutMission statutMission;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mission_agents",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    @Builder.Default
    private Set<AgentDeSecurite> agents = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "planning_id", nullable = true)
    private Planning planning;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "geolocalisation_id")
    private GeolocalisationGPS geolocalisationGPS;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<RapportIntervention> rapports = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TypeMission typeMission;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Pointage> pointages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contrat_id")
    private Contrat contrat;

    @Column(nullable = false)
    private Integer nombreAgents;

    @Column(nullable = false)
    private Integer quantite;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tarif_mission_id")
    private TarifMission tarif;

    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "missions")
    @Builder.Default
    private List<Facture> factures = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ContratDeTravail> contratsDeTravail = new ArrayList<>();

}