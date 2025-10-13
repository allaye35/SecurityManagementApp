package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.RapportStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RapportInterventionCreateDto {

    private LocalDateTime dateIntervention;

    private String description;

    private String contenu;

    private String agentNom;
    private String agentEmail;
    private String agentTelephone;

    private RapportStatus status;

    private Long missionId;
}
