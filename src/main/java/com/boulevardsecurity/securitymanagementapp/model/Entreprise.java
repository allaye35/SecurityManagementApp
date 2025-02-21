package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "entreprises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false, unique = true)
    private String telephone;

    // Une entreprise peut avoir plusieurs agents
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AgentDeSecurite> agents = new HashSet<>();

    // Une entreprise peut gérer plusieurs missions
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Mission> missions = new HashSet<>();

    // Une entreprise peut avoir plusieurs plannings
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Planning> plannings = new HashSet<>();

    // ===== MÉTHODES UTILITAIRES =====

    public void ajouterAgent(AgentDeSecurite agent) {
        agents.add(agent);
        agent.setEntreprise(this);
    }

    public void supprimerAgent(AgentDeSecurite agent) {
        agents.remove(agent);
        agent.setEntreprise(null);
    }

    public void ajouterMission(Mission mission) {
        missions.add(mission);
        mission.setEntreprise(this);
    }

    public void supprimerMission(Mission mission) {
        missions.remove(mission);
        mission.setEntreprise(null);
    }

    public void ajouterPlanning(Planning planning) {
        plannings.add(planning);
        planning.setEntreprise(this);
    }

    public void supprimerPlanning(Planning planning) {
        plannings.remove(planning);
        planning.setEntreprise(null);
    }
}
