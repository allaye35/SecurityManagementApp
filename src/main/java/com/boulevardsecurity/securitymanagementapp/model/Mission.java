package com.boulevardsecurity.securitymanagementapp.model;
import com.boulevardsecurity.securitymanagementapp.Enums.StatutMission;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeMission;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, length = 500)
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
    @JoinColumn(name = "planning_id", nullable = true)
    @ToString.Exclude
    private Planning planning;

    // Relation avec Site (Une mission se déroule dans un seul site)
    @ManyToOne
    @JoinColumn(name = "site_id", nullable = true)
    @ToString.Exclude
    private Site site;

    @ManyToOne
    @JoinColumn(name = "geolocalisation_id", nullable = true)
    private GeolocalisationGPS geolocalisationGPS;

    // Relation avec RapportIntervention (une mission peut avoir plusieurs rapports)
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RapportIntervention> rapports;

    // Association avec Entreprise
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    @Enumerated(EnumType.STRING)  // Utilisation de l'enum pour enregistrer le type de mission
    @Column(nullable = false)
    private TypeMission typeMission;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pointage> pointages = new ArrayList<>();  //

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    // ----------------------------------------------
    // RELATION AVEC Contrat (pour la copie éventuelle)
    // ----------------------------------------------
    @ManyToOne
    @JoinColumn(name = "contrat_id")
    private Contrat contrat;
}
