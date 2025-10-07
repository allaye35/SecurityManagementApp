// src/main/java/com/boulevardsecurity/securitymanagementapp/service/impl/PasswordResetServiceImpl.java
package com.boulevardsecurity.securitymanagementapp.service.impl;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.PasswordResetToken;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.PasswordResetTokenRepository;
import com.boulevardsecurity.securitymanagementapp.service.NotificationService;
import com.boulevardsecurity.securitymanagementapp.service.PasswordResetService;
import com.boulevardsecurity.securitymanagementapp.util.EmailUtil;
import com.boulevardsecurity.securitymanagementapp.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AgentDeSecuriteRepository agentRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.public-url:http://localhost:8080}")
    private String appPublicUrl;

    @Value("${app.password.reset.expiration-hours:2}")
    private long expirationHours;

    @Override
    @Transactional
    public void requestReset(String emailRaw) {
        String email = EmailUtil.normalize(emailRaw);
        Optional<AgentDeSecurite> opt = agentRepo.findByEmail(email);

        if (opt.isEmpty()) return;

        AgentDeSecurite agent = opt.get();

        String raw  = TokenUtil.generateRawToken();
        String hash = TokenUtil.sha256Hex(raw);

        PasswordResetToken token = PasswordResetToken.builder()
                .agentId(agent.getId())
                .tokenHash(hash)
                .expiresAt(Instant.now().plus(expirationHours, ChronoUnit.HOURS))
                .build();
        tokenRepo.save(token);

        String link = appPublicUrl + "/password-reset/confirm?token=" + raw;

        String subject = "Réinitialisation de votre mot de passe";
        String content = """
                Bonjour %s,

                Vous avez demandé à réinitialiser votre mot de passe.
                Cliquez sur le lien ci-dessous pour saisir un nouveau mot de passe :

                %s

                Ce lien expirera dans %d heures.

                Si vous n'avez pas demandé cette opération, ignorez cet email.
                """.formatted(agent.getNom(), link, expirationHours);

        notificationService.sendEmail(agent.getEmail(), subject, content);
    }

    @Override
    @Transactional
    public void confirmReset(String rawToken, String newPassword) {
        String hash = TokenUtil.sha256Hex(rawToken);

        PasswordResetToken token = tokenRepo.findByTokenHashAndConsumedAtIsNull(hash)
                .orElseThrow(() -> new IllegalArgumentException("Lien de réinitialisation invalide"));

        if (Instant.now().isAfter(token.getExpiresAt())) {
            throw new IllegalArgumentException("Lien de réinitialisation expiré");
        }

        AgentDeSecurite agent = agentRepo.findById(token.getAgentId())
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable pour ce token"));

        agent.setPassword(passwordEncoder.encode(newPassword));
        agent.setPasswordChangedAt(Instant.now());
        agentRepo.save(agent);

        token.setConsumedAt(Instant.now());
        tokenRepo.save(token);
    }
}
