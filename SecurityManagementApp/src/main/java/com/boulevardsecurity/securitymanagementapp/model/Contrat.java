package com.boulevardsecurity.securitymanagementapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contrats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referenceContrat;

    private LocalDate dateSignature;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @OneToMany(mappedBy = "contrat", cascade   = { CascadeType.PERSIST, CascadeType.MERGE })
    @Builder.Default
    private List<Mission> missions = new ArrayList<>();

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ArticleContrat> articles = new ArrayList<>();

    @Column(nullable = true)
    private Integer dureeMois;

    @Column(nullable = true)
    private Boolean taciteReconduction;

    @Column(nullable = true)
    private Integer preavisMois;

}
