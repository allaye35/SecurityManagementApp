package com.boulevardsecurity.securitymanagementapp.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationCreateDto {
    private String titre;
    private String message;
    private String destinataire;
}
