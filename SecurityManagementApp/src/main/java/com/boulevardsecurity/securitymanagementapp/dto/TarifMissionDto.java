package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeMission;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TarifMissionDto {

    private Long        id;
    private TypeMission typeMission;

    private BigDecimal  prixUnitaireHT;
    private BigDecimal  majorationNuit;
    private BigDecimal  majorationWeekend;
    private BigDecimal  majorationDimanche;
    private BigDecimal  majorationFerie;
    private BigDecimal  tauxTVA;

    private List<Long>  missionIds;
}
