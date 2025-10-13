package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.RapportInterventionCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.RapportInterventionDto;
import com.boulevardsecurity.securitymanagementapp.service.RapportInterventionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "http:
@RequiredArgsConstructor
public class RapportInterventionController {

    private final RapportInterventionService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<List<RapportInterventionDto>> getAll() {
        return ResponseEntity.ok(service.getAllRapports());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentReadRapport(authentication, #id)) or (hasAuthority('CLIENT') and @authz.canClientReadRapport(authentication, #id))")
    public ResponseEntity<RapportInterventionDto> getById(@PathVariable Long id) {
        return service.getRapportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mission/{missionId}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentWriteMission(authentication, #missionId)) or (hasAuthority('CLIENT') and @authz.canClientReadMission(authentication, #missionId))")
    public ResponseEntity<List<RapportInterventionDto>> getByMission(
            @PathVariable Long missionId
    ) {
        return ResponseEntity.ok(service.getRapportsByMissionId(missionId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<RapportInterventionDto> create(
            @RequestBody RapportInterventionCreateDto dto
    ) {
        RapportInterventionDto created = service.createRapport(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentWriteRapport(authentication, #id))")
    public ResponseEntity<RapportInterventionDto> update(
            @PathVariable Long id,
            @RequestBody RapportInterventionCreateDto dto
    ) {
        try {
            RapportInterventionDto updated = service.updateRapport(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentDeleteRapport(authentication, #id))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteRapport(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}