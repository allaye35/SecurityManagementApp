package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeZone;
import com.boulevardsecurity.securitymanagementapp.dto.AgentDeSecuriteDto;
import com.boulevardsecurity.securitymanagementapp.dto.ZoneDeTravailCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.ZoneDeTravailDto;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import com.boulevardsecurity.securitymanagementapp.service.ZoneDeTravailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "http:
@RequiredArgsConstructor
public class ZoneDeTravailController {

    private final ZoneDeTravailService service;
    private final AgentDeSecuriteService agentService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ZoneDeTravailDto> create(@RequestBody ZoneDeTravailCreateDto dto) {
        try {
            ZoneDeTravailDto created = service.createZone(dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<ZoneDeTravailDto>> getAll() {
        return ResponseEntity.ok(service.getAllZones());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ZoneDeTravailDto> getById(@PathVariable Long id) {
        return service.getZoneById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/recherche")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<ZoneDeTravailDto>> searchByName(@RequestParam String nom) {
        return ResponseEntity.ok(service.searchZonesByName(nom));
    }

    @GetMapping("/type/{typeZone}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<ZoneDeTravailDto>> searchByType(@PathVariable TypeZone typeZone) {
        return ResponseEntity.ok(service.searchZonesByType(typeZone));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ZoneDeTravailDto> update(
            @PathVariable Long id,
            @RequestBody ZoneDeTravailCreateDto dto
    ) {
        try {
            ZoneDeTravailDto updated = service.updateZone(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/agents")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<AgentDeSecuriteDto>> getAgentsByZoneId(@PathVariable Long id) {
        try {
            List<AgentDeSecuriteDto> agents = agentService.getAgentsByZoneId(id);
            return ResponseEntity.ok(agents);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{zoneId}/agents/{agentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<AgentDeSecuriteDto> assignAgentToZone(
            @PathVariable Long zoneId,
            @PathVariable Long agentId
    ) {
        try {
            AgentDeSecuriteDto dto = agentService.assignZoneDeTravail(agentId, zoneId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{zoneId}/agents/{agentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<Void> removeAgentFromZone(
            @PathVariable Long zoneId,
            @PathVariable Long agentId
    ) {
        try {
            agentService.removeFromZoneDeTravail(agentId, zoneId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteZone(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}