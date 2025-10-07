package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private LocalDate periodeDebut;

    @Column(nullable = false)
    private LocalDate periodeFin;

    private Double salaireDeBase;
    private Double heuresTravaillées;

    private Double primeNuit;
    private Double heuresSupplementaires;
    private Double primeDiverses;

    private Double totalCotisationsSalariales;
    private Double totalCotisationsEmployeur;

    private Double totalBrut;
    private Double netImposable;
    private Double netAPayer;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id")
    private AgentDeSecurite agentDeSecurite;

    @Lob
    private byte[] documentPdf;

    @OneToMany(mappedBy = "ficheDePaie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<LigneCotisation> lignesCotisation = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contrat_de_travail_id")
    private ContratDeTravail contratDeTravail;

}
