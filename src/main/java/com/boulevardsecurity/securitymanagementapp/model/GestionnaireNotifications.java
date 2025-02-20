package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GestionnaireNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String message;
    private String destinataire;
    private LocalDateTime dateEnvoi;

    @PrePersist
    public void prePersist() {
        this.dateEnvoi = LocalDateTime.now();
    }
}
