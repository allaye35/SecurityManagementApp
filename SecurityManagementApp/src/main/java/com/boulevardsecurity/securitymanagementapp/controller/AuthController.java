// src/main/java/com/boulevardsecurity/securitymanagementapp/controller/AuthController.java
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.dto.AgentDeSecuriteCreationDto;
import com.boulevardsecurity.securitymanagementapp.dto.AgentDeSecuriteDto;
import com.boulevardsecurity.securitymanagementapp.dto.ClientCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.ClientDto;
import com.boulevardsecurity.securitymanagementapp.dto.auth.LoginRequest;
import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import com.boulevardsecurity.securitymanagementapp.security.jwt.JwtService;
import com.boulevardsecurity.securitymanagementapp.security.user.AppUserDetails;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import com.boulevardsecurity.securitymanagementapp.service.ClientService;
import com.boulevardsecurity.securitymanagementapp.service.EmailVerificationService;
import com.boulevardsecurity.securitymanagementapp.service.PasswordResetService;
import com.boulevardsecurity.securitymanagementapp.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final AgentDeSecuriteRepository agentRepo;
    private final ClientRepository clientRepo;

    private final AgentDeSecuriteService agentService;
    private final ClientService clientService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String email = EmailUtil.normalize(req.getEmail());

        AgentDeSecurite a = agentRepo.findByEmail(email).orElse(null);
        if (a != null) {
            if (!a.isEmailVerified()) {
                return ResponseEntity.status(403).body(Map.of(
                        "message", "Adresse email non vérifiée. Consultez votre boîte mail pour activer le compte."
                ));
            }
            if (!a.isAdminApproved()) {
                return ResponseEntity.status(403).body(Map.of(
                        "message", "Compte en attente d’approbation par un administrateur."
                ));
            }
        }
        Client c = clientRepo.findByEmail(email).orElse(null);
        if (c != null) {
            if (!c.isEmailVerified()) {
                return ResponseEntity.status(403).body(Map.of(
                        "message", "Adresse email non vérifiée. Consultez votre boîte mail pour activer le compte."
                ));
            }
            if (!c.isAdminApproved()) {
                return ResponseEntity.status(403).body(Map.of(
                        "message", "Compte en attente d’approbation par un administrateur."
                ));
            }
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.getPassword())
            );
            AppUserDetails user = (AppUserDetails) auth.getPrincipal();

            String access  = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole(), user.getUserType());
            String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("accessToken",  access);
            body.put("refreshToken", refresh);
            body.put("userId",       user.getId());
            body.put("role",         user.getRole().name());
            body.put("userType",     user.getUserType());
            body.put("email",        user.getEmail());

            if ("AGENT".equals(user.getUserType()) && a != null) {
                if (a.getNom() != null)    body.put("nom", a.getNom());
                if (a.getPrenom() != null) body.put("prenom", a.getPrenom());
            } else if ("CLIENT".equals(user.getUserType()) && c != null) {
                if (c.getNom() != null)          body.put("nom", c.getNom());
                if (c.getPrenom() != null)       body.put("prenom", c.getPrenom());
                if (c.getRepresentant() != null) body.put("representant", c.getRepresentant());
                if (c.getSiege() != null)        body.put("siege", c.getSiege());
            }

            return ResponseEntity.ok(body);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe invalide."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refresh = body.get("refreshToken");
        if (refresh == null || !jwtService.isValid(refresh)) {
            return ResponseEntity.status(401).body(Map.of("message", "Refresh token invalide"));
        }
        String email = jwtService.getSubject(refresh);

        var agent = agentRepo.findByEmail(email).orElse(null);
        if (agent != null) {
            String access = jwtService.generateAccessToken(agent.getId(), agent.getEmail(), agent.getRole(), "AGENT");
            return ResponseEntity.ok(Map.of("accessToken", access));
        }
        var client = clientRepo.findByEmail(email).orElse(null);
        if (client != null) {
            String access = jwtService.generateAccessToken(client.getId(), client.getEmail(), client.getRole(), "CLIENT");
            return ResponseEntity.ok(Map.of("accessToken", access));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Utilisateur introuvable"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Déconnecté (supprime les tokens côté client)"));
    }

    @PostMapping("/register-agent")
    public ResponseEntity<?> registerAgent(@RequestBody AgentDeSecuriteCreationDto dto) {
        String normalized = EmailUtil.normalize(dto.getEmail());
        if (agentRepo.findByEmail(normalized).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message","Email déjà utilisé"));
        }
        dto.setEmail(normalized);
        dto.setRole(Role.AGENT_SECURITE);

        AgentDeSecuriteDto created = agentService.createAgent(dto);

        emailVerificationService.sendVerificationEmailForAgent(created.getId(), created.getEmail());

        return ResponseEntity.created(URI.create("/api/agents/" + created.getId()))
                .body(Map.of("message", "Compte créé. Vérifiez votre email pour activer le compte."));
    }

    @PostMapping("/register-client")
    public ResponseEntity<?> registerClient(@RequestBody ClientCreateDto dto) {
        String normalized = EmailUtil.normalize(dto.getEmail());
        if (clientRepo.findByEmail(normalized).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message","Email déjà utilisé"));
        }
        dto.setEmail(normalized);

        ClientDto created = clientService.createClient(dto);

        emailVerificationService.sendVerificationEmailForClient(created.getId(), created.getEmail());

        return ResponseEntity.created(URI.create("/api/clients/" + created.getId()))
                .body(Map.of("message", "Compte client créé. Vérifiez votre email pour activer le compte."));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam("token") String token) {
        try {
            emailVerificationService.confirmEmail(token);
            return ResponseEntity.ok(Map.of("message", "Email vérifié."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/verify-email/code")
    public ResponseEntity<?> verifyEmailByCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code  = body.get("code");
        if (email == null || email.isBlank() || code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "email et code sont requis"));
        }
        emailVerificationService.confirmEmailByCode(email, code);
        return ResponseEntity.ok(Map.of("message", "Email vérifié."));
    }

    @PostMapping("/verify-email/resend")
    public ResponseEntity<?> resendVerifyEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email requis."));
        }
        emailVerificationService.resend(email);
        return ResponseEntity.ok(Map.of("message", "Si un compte existe, un email de vérification a été envoyé."));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message","L'email est requis."));
        }
        passwordResetService.requestReset(email);
        return ResponseEntity.ok(Map.of("message","Si un compte existe pour cet email, un lien a été envoyé."));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        if (token == null || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message","Token et nouveau mot de passe requis."));
        }
        passwordResetService.confirmReset(token, newPassword);
        return ResponseEntity.ok(Map.of("message","Mot de passe mis à jour."));
    }

    @GetMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmPasswordResetGet(@RequestParam("token") String token,
                                                     @RequestParam(value="newPassword", required = false) String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message","Spécifiez newPassword en query, ou utilisez l'endpoint POST."));
        }
        passwordResetService.confirmReset(token, newPassword);
        return ResponseEntity.ok(Map.of("message","Mot de passe mis à jour."));
    }
}
