// src/main/java/com/boulevardsecurity/securitymanagementapp/model/EmailVerificationToken.java
package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.VerificationSubject;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "email_verification_tokens",
        indexes = {
                @Index(name = "idx_evt_tokenhash", columnList = "tokenHash"),
                @Index(name = "idx_evt_subject",   columnList = "subjectType,subjectId")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailVerificationToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Qui doit vérifier : AGENT ou CLIENT */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationSubject subjectType;

    /** L’id de l’agent ou du client */
    @Column(nullable = false)
    private Long subjectId;

    /** SHA-256 du token du lien (64 hex) */
    @Column(nullable = false, length = 64)
    private String tokenHash;

    /** (Optionnel) SHA-256 du code 6 chiffres (64 hex) pour la vérif par code */
    @Column(length = 64)
    private String codeHash;

    /** Date d’expiration */
    @Column(nullable = false)
    private Instant expiresAt;

    /** Renseigné quand le token est utilisé */
    private Instant consumedAt;
}
