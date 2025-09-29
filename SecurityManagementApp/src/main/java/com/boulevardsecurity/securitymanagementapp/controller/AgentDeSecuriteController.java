package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.dto.*;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentService;

    @GetMapping
    public ResponseEntity<List<AgentDeSecuriteDto>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentDeSecuriteDto> getAgentById(@PathVariable Long id) {
        return agentService.getAgentById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<AgentDeSecuriteDto> getAgentByEmail(@RequestParam String email) {
        return agentService.getAgentByEmail(email).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<AgentDeSecuriteDto> createAgent(@RequestBody AgentDeSecuriteCreationDto creationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agentService.createAgent(creationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentDeSecuriteDto> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecuriteCreationDto updateDto) {
        try { return ResponseEntity.ok(agentService.updateAgent(id, updateDto)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        try { agentService.deleteAgent(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PutMapping("/{agentId}/zone/{zoneId}")
    public ResponseEntity<AgentDeSecuriteDto> assignZone(@PathVariable Long agentId, @PathVariable Long zoneId) {
        try { return ResponseEntity.ok(agentService.assignZoneDeTravail(agentId, zoneId)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping("/{agentId}/disponibilites")
    public ResponseEntity<DisponibiliteDto> addDisponibilite(@PathVariable Long agentId, @RequestBody DisponibiliteCreationDto dispoDto) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(agentService.ajouterDisponibilite(agentId, dispoDto)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping("/{agentId}/cartesProfessionnelles")
    public ResponseEntity<CarteProfessionnelleDto> addCarte(@PathVariable Long agentId, @RequestBody CarteProfessionnelleCreationDto carteDto) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(agentService.ajouterCarteProfessionnelle(agentId, carteDto)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PutMapping("/{agentId}/role")
    public ResponseEntity<AgentDeSecuriteDto> changeRole(@PathVariable Long agentId, @RequestParam Role role) {
        try { return ResponseEntity.ok(agentService.changeRole(agentId, role)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping("/{agentId}/planning")
    public ResponseEntity<PlanningDto> getPlanning(@PathVariable Long agentId) {
        return agentService.getPlanningByAgentId(agentId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{agentId}/disponibilites/{disponibiliteId}")
    public ResponseEntity<AgentDeSecuriteDto> assignDisponibiliteExistante(@PathVariable Long agentId, @PathVariable Long disponibiliteId) {
        try { return ResponseEntity.ok(agentService.assignDisponibiliteExistante(agentId, disponibiliteId)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping("/{agentId}/diplomesSsiap")
    public ResponseEntity<DiplomeSsiapDto> addDiplome(@PathVariable Long agentId, @RequestBody DiplomeSsiapCreationDto diplomeDto) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(agentService.ajouterDiplomeSSIAP(agentId, diplomeDto)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PutMapping("/{agentId}/cartesProfessionnelles/{carteId}")
    public ResponseEntity<AgentDeSecuriteDto> assignCarteExistante(@PathVariable Long agentId, @PathVariable Long carteId) {
        try { return ResponseEntity.ok(agentService.assignCarteExistante(agentId, carteId)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PutMapping("/{agentId}/diplomesSsiap/{diplomeId}")
    public ResponseEntity<AgentDeSecuriteDto> assignDiplomeExistante(@PathVariable Long agentId, @PathVariable Long diplomeId) {
        try { return ResponseEntity.ok(agentService.assignDiplomeExistante(agentId, diplomeId)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }
}
