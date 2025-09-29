// src/main/java/com/boulevardsecurity/securitymanagementapp/controller/AdminAccountController.java
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import com.boulevardsecurity.securitymanagementapp.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AgentDeSecuriteRepository agentRepo;
    private final ClientRepository clientRepo;

    // ---- listes en attente (email vérifié mais pas approuvé)
    @GetMapping("/pending/agents")
    public List<AgentDeSecurite> pendingAgents() {
        return agentRepo.findAll().stream()
                .filter(a -> a.isEmailVerified() && !a.isAdminApproved())
                .toList();
    }

    @GetMapping("/pending/clients")
    public List<Client> pendingClients() {
        return clientRepo.findAll().stream()
                .filter(c -> c.isEmailVerified() && !c.isAdminApproved())
                .toList();
    }

    // ---- actions
    @PostMapping("/agents/{id}/approve")
    public ResponseEntity<?> approveAgent(@PathVariable Long id, Authentication auth) {
        AgentDeSecurite a = agentRepo.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();
        a.setAdminApproved(true);
        a.setAdminApprovedAt(Instant.now());
        a.setAdminApprovedById(getAdminId(auth));
        agentRepo.save(a);
        return ResponseEntity.ok(Map.of("message","Agent approuvé."));
    }

    @PostMapping("/clients/{id}/approve")
    public ResponseEntity<?> approveClient(@PathVariable Long id, Authentication auth) {
        Client c = clientRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();
        c.setAdminApproved(true);
        c.setAdminApprovedAt(Instant.now());
        c.setAdminApprovedById(getAdminId(auth));
        clientRepo.save(c);
        return ResponseEntity.ok(Map.of("message","Client approuvé."));
    }

    private Long getAdminId(Authentication auth) {
        try { return ((AppUserDetails) auth.getPrincipal()).getId(); }
        catch (Exception e) { return null; }
    }
}
