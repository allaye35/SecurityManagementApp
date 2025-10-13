package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.StatutDevis;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DevisDto {

    private Long        id;
    private String      referenceDevis;
    private String      description;
    private StatutDevis statut;

    private LocalDate   dateCreation;
    private LocalDate   dateValidite;

    private String      conditionsGenerales;

    private Long        entrepriseId;
    private Long        clientId;
    private Long        contratId;
    private List<Long>  missionIds;

    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
}
