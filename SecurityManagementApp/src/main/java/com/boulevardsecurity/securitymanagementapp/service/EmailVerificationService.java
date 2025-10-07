// src/main/java/com/boulevardsecurity/securitymanagementapp/service/EmailVerificationService.java
package com.boulevardsecurity.securitymanagementapp.service;

public interface EmailVerificationService {

    void sendVerificationEmailForAgent(Long agentId, String emailRaw);

    void sendVerificationEmailForClient(Long clientId, String emailRaw);

    void resend(String emailRaw);

    void confirmEmail(String rawToken);

    void confirmEmailByCode(String emailRaw, String codePlain);
}
