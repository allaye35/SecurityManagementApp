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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationSubject subjectType;

    @Column(nullable = false)
    private Long subjectId;

    @Column(nullable = false, length = 64)
    private String tokenHash;

    @Column(length = 64)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant consumedAt;
}