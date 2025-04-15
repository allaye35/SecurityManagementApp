package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.StatutFacture;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Représente la facture générée pour un client, à partir d'un devis.
 */
@Entity
@Table(name = "factures")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numéro unique identifiant la facture (ex: F2023-0001).
     */
    @Column(nullable = false, unique = true)
    private String numeroFacture;

    /**
     * Date d'émission de la facture.
     */
    @Column(nullable = false)
    private LocalDate dateEmission;

    /**
     * Montant total Hors Taxes (recalculé via `recalculerMontants()`).
     */
    @Column(nullable = false)
    private Double totalHT;

    /**
     * Taux de TVA applicable (ex: 20.0 pour 20%).
     */
    @Column(nullable = false)
    private Double tauxTVA;

    /**
     * Montant total TTC (HT + TVA).
     */
    @Column(nullable = false)
    private Double totalTTC;

    /**
     * Statut de la facture (EN_ATTENTE, PAYEE, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut;

    /**
     * Date de paiement, si la facture est payée.
     */
    private LocalDate datePaiement;

    /**
     * Relation 1-1 avec le Devis (ex: devis ACCEPTÉ).
     */
    @OneToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    // (Accès indirect au client depuis le devis, si besoin)
    @Transient
    public Client getClient() {
        return (devis != null) ? devis.getClient() : null;
    }

    // ========================= MÉTHODES UTILES ===========================

    /**
     * Recalcule les montants (HT/TTC) en fonction des champs du Devis.
     */
    public void recalculerMontants() {
        if (devis != null) {
            // Exemple : utiliser montantHT / montantTTC existants dans Devis
            if (devis.getMontantHT() != null && devis.getMontantTTC() != null) {
                this.totalHT = devis.getMontantHT().doubleValue();
                this.totalTTC = devis.getMontantTTC().doubleValue();
                return;
            }
            // Sinon, on se base sur devis.getMontant() et on applique la TVA
            if (devis.getMontant() != null && tauxTVA != null) {
                this.totalHT = devis.getMontant().doubleValue();
                this.totalTTC = this.totalHT * (1 + (tauxTVA / 100.0));
                return;
            }
        }
        // Si rien n’est disponible
        this.totalHT = 0.0;
        this.totalTTC = 0.0;
    }

    /**
     * Marque la facture comme payée, enregistre la date de paiement.
     */
    public void marquerCommePayee() {
        this.statut = StatutFacture.PAYEE;
        this.datePaiement = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", numeroFacture='" + numeroFacture + '\'' +
                ", dateEmission=" + dateEmission +
                ", totalHT=" + totalHT +
                ", tauxTVA=" + tauxTVA +
                ", totalTTC=" + totalTTC +
                ", statut=" + statut +
                ", datePaiement=" + datePaiement +
                ", devis=" + (devis != null ? devis.getId() : null) +
                '}';
    }
}
