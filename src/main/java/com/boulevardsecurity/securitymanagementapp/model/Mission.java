package com.boulevardsecurity.securitymanagementapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
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

    // Une mission appartient à une seule entreprise
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Une mission est affectée à plusieurs agents et un agent peut avoir plusieurs missions
    @ManyToMany
    @JoinTable(
            name = "mission_agents",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    @JsonIgnore // Évite la récursion infinie dans les API REST
    private Set<AgentDeSecurite> agents = new HashSet<>();

    // Une mission est liée à un seul planning
    @ManyToOne
    @JoinColumn(name = "planning_id", nullable = false)
    private Planning planning;

    // ======= MÉTHODES UTILITAIRES =======

    public void ajouterAgent(AgentDeSecurite agent) {
        if (!this.agents.contains(agent)) {
            this.agents.add(agent);
            agent.getMissions().add(this);
        }
    }

    public void supprimerAgent(AgentDeSecurite agent) {
        if (this.agents.contains(agent)) {
            this.agents.remove(agent);
            agent.getMissions().remove(this);
        }
    }

    public boolean contientAgent(AgentDeSecurite agent) {
        return this.agents.contains(agent);
    }

    public int getNombreAgents() {
        return this.agents.size();
    }

    public boolean estEnCours() {
        LocalDate aujourdHui = LocalDate.now();
        return (aujourdHui.isAfter(dateDebut) || aujourdHui.isEqual(dateDebut)) &&
                (aujourdHui.isBefore(dateFin) || aujourdHui.isEqual(dateFin));
    }

    public boolean estTerminee() {
        return LocalDate.now().isAfter(dateFin);
    }
}
