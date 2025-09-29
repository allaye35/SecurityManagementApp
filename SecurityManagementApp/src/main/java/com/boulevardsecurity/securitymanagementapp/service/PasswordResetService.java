package com.boulevardsecurity.securitymanagementapp.service;

public interface PasswordResetService {
    /** Crée un token et envoie l’email de réinitialisation */
    void requestReset(String email);

    /** Valide le token et remplace le mot de passe */
    void confirmReset(String rawToken, String newPassword);
}
