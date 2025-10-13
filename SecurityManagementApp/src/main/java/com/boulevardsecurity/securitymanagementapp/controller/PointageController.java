// src/main/java/com/boulevardsecurity/securitymanagementapp/controller/PointageController.java
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.AgentDeSecuriteDto;
import com.boulevardsecurity.securitymanagementapp.dto.PointageCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.PointageDto;
import com.boulevardsecurity.securitymanagementapp.service.PointageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/pointages")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PointageController {

    private final PointageService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<PointageDto>> recupererTous() {
        return ResponseEntity.ok(service.recupererTousLesPointages());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentWritePointage(authentication, #id)) or (hasAuthority('CLIENT') and @authz.canClientReadPointage(authentication, #id))")
    public ResponseEntity<PointageDto> recupererParId(@PathVariable Long id) {
        return service.recupererPointageParId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/mission/{idMission}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentWriteMission(authentication, #idMission)) or (hasAuthority('CLIENT') and @authz.canClientReadMission(authentication, #idMission))")
    public ResponseEntity<List<PointageDto>> recupererParMission(@PathVariable Long idMission) {
        return ResponseEntity.ok(service.recupererPointagesParMission(idMission));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<PointageDto> creer(@RequestBody PointageCreateDto dto) {
        try {
            PointageDto cree = service.creerPointage(dto);
            return ResponseEntity.ok(cree);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PointageDto> modifier(
            @PathVariable Long id,
            @RequestBody PointageCreateDto dto
    ) {
        try {
            PointageDto modifie = service.modifierPointage(id, dto);
            return ResponseEntity.ok(modifie);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        try {
            service.supprimerPointage(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/prise-service")
    @PreAuthorize("hasAuthority('AGENT_SECURITE')")
    public ResponseEntity<?> priseDeService(@RequestBody PointageCreateDto dto) {
        try {
            PointageDto pointage = service.enregistrerPriseDeService(dto);
            return ResponseEntity.ok(pointage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/fin-service")
    @PreAuthorize("hasAuthority('AGENT_SECURITE')")
    public ResponseEntity<?> finDeService(@RequestBody PointageCreateDto dto) {
        try {
            PointageDto pointage = service.enregistrerFinDeService(dto);
            return ResponseEntity.ok(pointage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erreur serveur: " + e.getMessage()));
        }
    }

    @GetMapping("/mission/{idMission}/agents-en-service")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<List<AgentDeSecuriteDto>> getAgentsEnService(@PathVariable Long idMission) {
        try {
            List<AgentDeSecuriteDto> agents = service.getAgentsEnService(idMission);
            return ResponseEntity.ok(agents);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
