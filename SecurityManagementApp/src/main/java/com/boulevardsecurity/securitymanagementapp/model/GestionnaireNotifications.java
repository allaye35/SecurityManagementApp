package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeNotification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

    @Entity
    @Table(name = "notifications")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public class GestionnaireNotifications {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String titre;

        @Column(columnDefinition = "TEXT")
        private String message;

        @Column(nullable = false)
        private String destinataire;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TypeNotification typeNotification;

        @Column(nullable = false)
        @Builder.Default
        private boolean lu = false;

        @Column(nullable = false)
        private LocalDateTime dateEnvoi;

        @PrePersist
        public void prePersist() {
            this.dateEnvoi = LocalDateTime.now();
        }

        @ManyToOne
        @JoinColumn(name = "agent_id")
        private AgentDeSecurite agentDeSecurite;

        @ManyToOne
        @JoinColumn(name = "client_id")
        private Client client;

    }

