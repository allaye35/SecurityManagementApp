// src/main/java/com/boulevardsecurity/securitymanagementapp/controller/AdminAccountController.java
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import com.boulevardsecurity.securitymanagementapp.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AgentDeSecuriteRepository agentRepo;
    private final ClientRepository clientRepo;

    @GetMapping("/pending/agents")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AgentDeSecurite> pendingAgents() {
        return agentRepo.findByEmailVerifiedTrueAndAdminApprovedFalse();
    }

    @GetMapping("/pending/clients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Client> pendingClients() {
        return clientRepo.findByEmailVerifiedTrueAndAdminApprovedFalse();
    }

    @PostMapping("/agents/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveAgent(@PathVariable Long id, Authentication auth) {
        var a = agentRepo.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();

        a.setAdminApproved(true);
        a.setAdminApprovedAt(Instant.now());
        a.setAdminApprovedById(getAdminId(auth));
        agentRepo.save(a);

        return ResponseEntity.ok(Map.of("message", "Agent approuvé."));
    }

    @PostMapping("/clients/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveClient(@PathVariable Long id, Authentication auth) {
        var c = clientRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        c.setAdminApproved(true);
        c.setAdminApprovedAt(Instant.now());
        c.setAdminApprovedById(getAdminId(auth));
        clientRepo.save(c);

        return ResponseEntity.ok(Map.of("message", "Client approuvé."));
    }

    @PostMapping("/agents/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectAgent(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication auth
    ) {
        var a = agentRepo.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();

        agentRepo.delete(a);

        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return ResponseEntity.ok(Map.of(
                "message", "Agent refusé" + (reason.isBlank() ? "." : " : " + reason)
        ));
    }

    @PostMapping("/clients/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectClient(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication auth
    ) {
        var c = clientRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        clientRepo.delete(c);

        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return ResponseEntity.ok(Map.of(
                "message", "Client refusé" + (reason.isBlank() ? "." : " : " + reason)
        ));
    }

    private Long getAdminId(Authentication auth) {
        try {
            return ((AppUserDetails) auth.getPrincipal()).getId();
        } catch (Exception e) {
            return null;
        }
    }
}
