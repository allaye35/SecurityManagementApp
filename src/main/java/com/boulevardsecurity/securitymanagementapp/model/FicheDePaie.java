package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Représente une fiche de paie (bulletin de salaire) liée à un contrat de travail.
 */
@Entity
@Table(name = "fiches_de_paie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheDePaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Référence unique du bulletin de paie (ex: FP-2024-001).
     */
    @Column(nullable = false, unique = true)
    private String referenceBulletin;

    /**
     * Date d'émission de la fiche de paie.
     */
    @Column(nullable = false)
    private LocalDate dateEmission;

    /**
     * Période concernée (ex: mois de janvier).
     * Tu peux stocker deux dates (débutPeriode, finPeriode) pour être précis.
     */
    private LocalDate debutPeriode;
    private LocalDate finPeriode;

    /**
     * Salaire brut (avant déductions).
     */
    @Column(nullable = false)
    private Double salaireBrut;

    /**
     * Montant total des cotisations sociales prélevées.
     */
    private Double cotisations;

    /**
     * Salaire net à payer (salaireBrut - cotisations + éventuelles primes).
     */
    @Column(nullable = false)
    private Double salaireNet;

    /**
     * Primes spécifiques (nuit, transport, etc.).
     */
    private Double primeNuit;
    private Double primeTransport;

    /**
     * Relation avec le contrat de travail :
     * Plusieurs fiches de paie peuvent se rattacher au même contrat.
     */
    @ManyToOne
    @JoinColumn(name = "contrat_travail_id", nullable = false)
    private ContratDeTravail contratDeTravail;
}
