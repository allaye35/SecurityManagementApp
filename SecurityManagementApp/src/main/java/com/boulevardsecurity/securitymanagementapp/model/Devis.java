package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.StatutDevis;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "devis")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 50)
    private String referenceDevis;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private StatutDevis statut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private LocalDate dateValidite;

    @Column(columnDefinition = "TEXT")
    private String conditionsGenerales;

    @OneToOne(mappedBy = "devis",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private Contrat contrat;

    @OneToMany(
            mappedBy = "devis",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private List<Mission> missions = new ArrayList<>();

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal montantHT = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal montantTVA = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal montantTTC = BigDecimal.ZERO;

    public void recalculerTotaux() {
        this.montantHT = missions.stream()
                .map(m -> m.getMontantHT() == null ? BigDecimal.ZERO : m.getMontantHT())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.montantTVA = missions.stream()
                .map(m -> m.getMontantTVA() == null ? BigDecimal.ZERO : m.getMontantTVA())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.montantTTC = missions.stream()
                .map(m -> m.getMontantTTC() == null ? BigDecimal.ZERO : m.getMontantTTC())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
