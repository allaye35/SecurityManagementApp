package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeMission;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tarifs_mission")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifMission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TypeMission typeMission;

    @Column(nullable = false)
    private BigDecimal prixUnitaireHT;

    @Column(nullable = false)
    private BigDecimal majorationNuit;

    @Column(nullable = false)
    private BigDecimal majorationWeekend;

    @Column(nullable = false)
    private BigDecimal majorationDimanche;

    @Column(nullable = false)
    private BigDecimal majorationFerie;

    @Column(nullable = false)
    private BigDecimal tauxTVA;
}
