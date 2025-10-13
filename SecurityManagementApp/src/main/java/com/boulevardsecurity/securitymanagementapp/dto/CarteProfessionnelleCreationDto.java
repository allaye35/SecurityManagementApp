package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeCarteProfessionnelle;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CarteProfessionnelleCreationDto {

    @NotNull
    private TypeCarteProfessionnelle typeCarte;

    @NotNull @NonNull
    private String numeroCarte;

    @NotNull
    private Date dateDebut;

    @NotNull
    private Date dateFin;

    @NotNull
    private Long agentId;
}
