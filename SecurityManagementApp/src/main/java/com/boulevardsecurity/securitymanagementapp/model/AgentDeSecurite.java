package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.Enums.StatutAgent;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "agents_de_securite")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"missions", "disponibilites"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AgentDeSecurite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)  private String nom;
    @Column(nullable = false)  private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(unique = true)   private String telephone;
    private String adresse;
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    private StatutAgent statut;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.AGENT_SECURITE;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean adminApproved = false;

    private Instant adminApprovedAt;
    private Long adminApprovedById;

    private Instant passwordChangedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "agents_zones",
            joinColumns = @JoinColumn(name = "agent_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    @Builder.Default
    private Set<ZoneDeTravail> zonesDeTravail = new HashSet<>();

    @ManyToMany(mappedBy = "agents", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Mission> missions = new HashSet<>();

    @OneToMany(mappedBy = "agentDeSecurite", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Disponibilite> disponibilites = new ArrayList<>();

    @OneToMany(mappedBy = "agentDeSecurite", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CarteProfessionnelle> cartesProfessionnelles = new ArrayList<>();

    @OneToMany(mappedBy = "agentDeSecurite", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<DiplomeSSIAP> diplomesSSIAP = new ArrayList<>();

    @OneToMany(mappedBy = "agentDeSecurite", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ContratDeTravail> contratsDeTravail = new ArrayList<>();

    @OneToMany(mappedBy = "agentDeSecurite", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<GestionnaireNotifications> notifications = new ArrayList<>();
}