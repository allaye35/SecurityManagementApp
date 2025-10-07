package com.boulevardsecurity.securitymanagementapp.service;

public interface PasswordResetService {
    
    void requestReset(String email);

    void confirmReset(String rawToken, String newPassword);
}
