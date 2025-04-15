package com.boulevardsecurity.securitymanagementapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un contrat de travail liant un agent de sécurité à l'entreprise.
 */
@Entity
@Table(name = "contrats_de_travail")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratDeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * L'agent concerné par ce contrat de travail.
     * Un agent peut avoir plusieurs contrats successifs (CDD, CDI, etc.).
     */
    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentDeSecurite agentDeSecurite;

    /**
     * Par exemple : CDI, CDD, Interim, Stage, etc.
     * Tu peux aussi créer un Enum TypeContrat si tu veux limiter les valeurs.
     */
    @Column(nullable = false)
    private String typeContrat;  // "CDI", "CDD", ...

    /**
     * Date de début du contrat.
     */
    private LocalDate dateDebut;

    /**
     * Date de fin du contrat (peut être null pour un CDI).
     */
    private LocalDate dateFin;

    /**
     * Salaire de base (mensuel ou horaire selon tes besoins).
     */
    private Double salaireDeBase;

    /**
     * Liste des fiches de paie associées à ce contrat.
     * Plusieurs bulletins peuvent être émis sur la durée du contrat.
     */
    @OneToMany(mappedBy = "contratDeTravail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FicheDePaie> fichesDePaie = new ArrayList<>();
}
