// src/main/java/com/boulevardsecurity/securitymanagementapp/dto/RapportInterventionDto.java
package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.RapportStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RapportInterventionDto {

    private Long            id;
    private LocalDateTime   dateIntervention;
    private String          description;
    private String          contenu;

    private String          agentNom;
    private String          agentEmail;
    private String          agentTelephone;

    private RapportStatus   status;

    private LocalDateTime   dateCreation;
    private LocalDateTime   dateModification;

    private Long            missionId;
}
