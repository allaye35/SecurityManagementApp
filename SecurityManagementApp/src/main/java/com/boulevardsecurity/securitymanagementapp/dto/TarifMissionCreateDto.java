package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeMission;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TarifMissionCreateDto {
    
    @NotNull
    private TypeMission typeMission;

    @NotNull @Positive
    private BigDecimal prixUnitaireHT;

    @NotNull
    private BigDecimal majorationNuit;

    @NotNull
    private BigDecimal majorationWeekend;

    @NotNull
    private BigDecimal majorationDimanche;

    @NotNull
    private BigDecimal majorationFerie;

    @NotNull
    private BigDecimal tauxTVA;
}
