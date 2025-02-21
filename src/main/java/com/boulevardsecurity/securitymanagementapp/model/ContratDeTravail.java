package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contrats_de_travail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ContratDeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentDeSecurite agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @OneToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @NotBlank(message = "Signature électronique requise")
    private String signatureElectronique;

    // ✅ Vérifier si le contrat est toujours valide
    public boolean estActif() {
        LocalDate today = LocalDate.now();
        return (dateDebut.isBefore(today) || dateDebut.isEqual(today)) && dateFin.isAfter(today);
    }

    // ✅ Prolonger la date de fin du contrat
    public void prolongerContrat(LocalDate nouvelleDateFin) {
        if (nouvelleDateFin.isAfter(this.dateFin)) {
            this.dateFin = nouvelleDateFin;
        } else {
            throw new IllegalArgumentException("La nouvelle date doit être postérieure à l'ancienne date de fin.");
        }
    }
}
