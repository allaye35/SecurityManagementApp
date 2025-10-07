package com.boulevardsecurity.securitymanagementapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDto {
    private Long id;
    private String titre;
    private String message;
    private String destinataire;
    private LocalDateTime dateEnvoi;
}

