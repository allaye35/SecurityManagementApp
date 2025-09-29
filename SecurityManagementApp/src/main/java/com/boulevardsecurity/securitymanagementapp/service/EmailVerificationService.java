// src/main/java/com/boulevardsecurity/securitymanagementapp/service/EmailVerificationService.java
package com.boulevardsecurity.securitymanagementapp.service;

public interface EmailVerificationService {

    /** Envoi après inscription (AGENT). */
    void sendVerificationEmailForAgent(Long agentId, String emailRaw);

    /** Envoi après inscription (CLIENT). */
    void sendVerificationEmailForClient(Long clientId, String emailRaw);

    /** Renvoyer un email de vérification (détecte agent ou client à partir de l'email). */
    void resend(String emailRaw);

    /** Confirmation via lien (token en query). */
    void confirmEmail(String rawToken);

    /** Confirmation via code (email + code 6 chiffres). */
    void confirmEmailByCode(String emailRaw, String codePlain);
}
