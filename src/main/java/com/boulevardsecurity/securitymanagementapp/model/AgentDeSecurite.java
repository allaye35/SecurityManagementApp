package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.StatutAgent;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "agents_de_securite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AgentDeSecurite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Column(nullable = false)
    private Float salaire;

    @Column(nullable = false)
    private String zoneDeTravail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAgent statut; // EN_SERVICE, EN_CONGE, ABSENT

    // Un agent appartient à une entreprise
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    @JsonIgnore // Ignore l’entreprise pour éviter la récursion infinie
    private Entreprise entreprise;



    // Un agent est inclus dans un planning (relation corrigée)
    @ManyToOne
    @JoinColumn(name = "planning_id")
    @JsonIgnore // Évite la récursion infinie
    private Planning planning;

    // Un agent peut être affecté à plusieurs missions
    @ManyToMany(mappedBy = "agents", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Mission> missions = new HashSet<>();

    // ======= MÉTHODES UTILITAIRES =======

    public void ajouterMission(Mission mission) {
        missions.add(mission);
        mission.getAgents().add(this);
    }

    public void supprimerMission(Mission mission) {
        missions.remove(mission);
        mission.getAgents().remove(this);
    }

    public boolean estDisponible() {
        return this.statut == StatutAgent.EN_SERVICE;
    }
}
