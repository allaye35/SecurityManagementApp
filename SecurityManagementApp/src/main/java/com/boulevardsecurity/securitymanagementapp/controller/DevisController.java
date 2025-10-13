// src/main/java/com/boulevardsecurity/securitymanagementapp/controller/DevisController.java
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.DevisCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.DevisDto;
import com.boulevardsecurity.securitymanagementapp.dto.ErrorResponseDto;
import com.boulevardsecurity.securitymanagementapp.service.DevisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Slf4j
public class DevisController {

    private final DevisService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<List<DevisDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<List<DevisDto>> getDevisDisponibles() {
        return ResponseEntity.ok(service.getDevisDisponibles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AGENT_SECURITE') or (hasAuthority('CLIENT') and @authz.canClientReadDevis(authentication, #id))")
    public ResponseEntity<DevisDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<DevisDto> getByReference(@RequestParam String reference) {
        return service.getByReference(reference)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> create(@RequestBody DevisCreateDto dto) {
        try {
            DevisDto created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            log.error("Erreur lors de la création du devis", ex);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDto(400, ex.getMessage()));
        } catch (Exception ex) {
            log.error("Erreur inattendue lors de la création du devis", ex);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDto(500, "Erreur serveur: " + ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DevisCreateDto dto
    ) {
        try {
            DevisDto updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la mise à jour du devis: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDto(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur serveur lors de la mise à jour du devis", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDto(500, "Erreur serveur: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la suppression du devis: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDto(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur serveur lors de la suppression du devis", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDto(500, "Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/missions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> ajouterMissions(
            @PathVariable Long id,
            @RequestBody List<Long> missionIds) {
        try {
            DevisDto dto = service.ajouterMissions(id, missionIds);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDto(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur serveur ajout missions devis", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDto(500, "Erreur serveur: " + e.getMessage()));
        }
    }
}
