// src/main/java/com/boulevardsecurity/securitymanagementapp/service/impl/EmailVerificationServiceImpl.java
package com.boulevardsecurity.securitymanagementapp.service.impl;

import com.boulevardsecurity.securitymanagementapp.Enums.VerificationSubject;
import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.model.EmailVerificationToken;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import com.boulevardsecurity.securitymanagementapp.repository.EmailVerificationTokenRepository;
import com.boulevardsecurity.securitymanagementapp.service.EmailVerificationService;
import com.boulevardsecurity.securitymanagementapp.service.NotificationService;
import com.boulevardsecurity.securitymanagementapp.util.EmailUtil;
import com.boulevardsecurity.securitymanagementapp.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final AgentDeSecuriteRepository agentRepo;
    private final ClientRepository clientRepo;
    private final EmailVerificationTokenRepository tokenRepo;
    private final NotificationService notificationService;

    @Value("${app.public-url:http://localhost:3000}")
    private String appPublicUrl;

    @Value("${app.email.verification.expiration-hours:24}")
    private long expirationHours;

    @Override
    @Transactional
    public void sendVerificationEmailForAgent(Long agentId, String emailRaw) {
        AgentDeSecurite a = agentRepo.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable: " + agentId));
        doSend(VerificationSubject.AGENT, a.getId(), a.getNom(), a.getEmail(), emailRaw);
    }

    @Override
    @Transactional
    public void sendVerificationEmailForClient(Long clientId, String emailRaw) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable: " + clientId));
        String displayName = c.getNom() != null ? c.getNom()
                : (c.getRepresentant() != null ? c.getRepresentant() : "client");
        doSend(VerificationSubject.CLIENT, c.getId(), displayName, c.getEmail(), emailRaw);
    }

    @Override
    @Transactional
    public void resend(String emailRaw) {
        String email = EmailUtil.normalize(emailRaw);

        AgentDeSecurite a = agentRepo.findByEmail(email).orElse(null);
        if (a != null) {
            doSend(VerificationSubject.AGENT, a.getId(), a.getNom(), a.getEmail(), email);
            return;
        }

        Client c = clientRepo.findByEmail(email).orElse(null);
        if (c != null) {
            String displayName = c.getNom() != null ? c.getNom()
                    : (c.getRepresentant() != null ? c.getRepresentant() : "client");
            doSend(VerificationSubject.CLIENT, c.getId(), displayName, c.getEmail(), email);
            return;
        }

    }

    private void doSend(VerificationSubject subjectType, Long subjectId, String displayName,
                        String storedEmail, String emailRaw) {

        String email = EmailUtil.normalize(emailRaw);
        if (!EmailUtil.equalsNormalized(storedEmail, email)) {
            email = storedEmail;
        }

        String raw  = TokenUtil.generateRawToken();
        String hash = TokenUtil.sha256Hex(raw);

        String code = TokenUtil.generateNumericCode(6);
        String codeHash = TokenUtil.sha256Hex(code);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .subjectType(subjectType)
                .subjectId(subjectId)
                .tokenHash(hash)
                .codeHash(codeHash)
                .expiresAt(Instant.now().plus(expirationHours, ChronoUnit.HOURS))
                .build();
        tokenRepo.save(token);

        String link = appPublicUrl + "/verify-email?token=" + raw;

        String subject = "Vérification de votre email";
        String content = """
                Bonjour %s,

                Merci pour votre inscription.

                ➤ Vérifiez votre adresse en cliquant :
                %s

                OU
                ➤ Saisissez ce code dans l’application : %s

                (Lien et code expirent dans %d heures.)
                """.formatted(displayName, link, code, expirationHours);

        notificationService.sendEmail(email, subject, content);
    }

    @Override
    @Transactional
    public void confirmEmail(String rawToken) {
        String hash = TokenUtil.sha256Hex(rawToken);
        EmailVerificationToken token = tokenRepo.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Token de vérification invalide"));

        if (Instant.now().isAfter(token.getExpiresAt()))
            throw new IllegalArgumentException("Token de vérification expiré");

        if (token.getConsumedAt() != null) return;

        if (token.getSubjectType() == VerificationSubject.AGENT) {
            AgentDeSecurite a = agentRepo.findById(token.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Agent introuvable pour ce token"));
            if (!a.isEmailVerified()) { a.setEmailVerified(true); agentRepo.save(a); }
        } else {
            Client c = clientRepo.findById(token.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Client introuvable pour ce token"));
            if (!c.isEmailVerified()) { c.setEmailVerified(true); clientRepo.save(c); }
        }

        token.setConsumedAt(Instant.now());
        tokenRepo.save(token);
    }

    @Override
    @Transactional
    public void confirmEmailByCode(String emailRaw, String codePlain) {
        String email = EmailUtil.normalize(emailRaw);

        AgentDeSecurite a = agentRepo.findByEmail(email).orElse(null);
        if (a != null) {
            String codeHash = TokenUtil.sha256Hex(codePlain);
            var token = tokenRepo.findBySubjectIdAndSubjectTypeAndCodeHashAndConsumedAtIsNull(
                    a.getId(), VerificationSubject.AGENT, codeHash
            ).orElseThrow(() -> new IllegalArgumentException("Code invalide."));
            if (Instant.now().isAfter(token.getExpiresAt()))
                throw new IllegalArgumentException("Code expiré.");

            a.setEmailVerified(true); agentRepo.save(a);
            token.setConsumedAt(Instant.now()); tokenRepo.save(token);
            return;
        }

        Client c = clientRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Aucun compte pour cet email."));
        String codeHash = TokenUtil.sha256Hex(codePlain);
        var token = tokenRepo.findBySubjectIdAndSubjectTypeAndCodeHashAndConsumedAtIsNull(
                c.getId(), VerificationSubject.CLIENT, codeHash
        ).orElseThrow(() -> new IllegalArgumentException("Code invalide."));
        if (Instant.now().isAfter(token.getExpiresAt()))
            throw new IllegalArgumentException("Code expiré.");

        c.setEmailVerified(true); clientRepo.save(c);
        token.setConsumedAt(Instant.now()); tokenRepo.save(token);
    }
}
