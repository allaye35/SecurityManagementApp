package com.boulevardsecurity.securitymanagementapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service de notification (email + SMS).
 * - Utilise app.mail.from comme adresse d’expéditeur si renseignée.
 * - Sinon, laisse Gmail définir l’expéditeur (compte SMTP).
 * - Envoi SMS via Textbelt (clé dans textbelt.api.key).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    /** Adresse d’expéditeur par défaut, ex: Boulevard Sécurité <allaye35@gmail.com> */
    @Value("${app.mail.from:}")
    private String defaultFrom;

    /** Clé Textbelt (gratuite par défaut = "textbelt", limité) */
    @Value("${textbelt.api.key:textbelt}")
    private String textbeltApiKey;

    /**
     * Envoi d’un email en texte brut.
     * Si app.mail.from est renseigné, on fait message.setFrom(defaultFrom)
     * (Gmail acceptera un alias "Send As" validé ; sinon il remplacera par le compte SMTP).
     */
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (StringUtils.hasText(defaultFrom)) {
                message.setFrom(defaultFrom);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("✅ Email envoyé à {}", to);
        } catch (Exception e) {
            log.error("❌ Erreur envoi email: {}", e.getMessage(), e);
        }
    }

    /**
     * Envoi d’un SMS avec Textbelt (1 SMS gratuit / jour avec la clé "textbelt").
     * Pour la prod, achète une clé sur https://textbelt.com/.
     */
    public void sendSMS(String phoneNumber, String message) {
        String apiUrl = "https://textbelt.com/text";

        WebClient webClient = WebClient.builder().baseUrl(apiUrl).build();

        webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "phone": "%s",
                      "message": "%s",
                      "key": "%s"
                    }
                    """.formatted(phoneNumber, message, textbeltApiKey))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(ex -> {
                    log.error("❌ Erreur envoi SMS: {}", ex.getMessage());
                    return Mono.empty();
                })
                .subscribe(response -> log.info("Réponse Textbelt: {}", response));
    }
}
