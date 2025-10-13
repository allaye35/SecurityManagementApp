package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.DisponibiliteCreationDto;
import com.boulevardsecurity.securitymanagementapp.dto.DisponibiliteDto;
import com.boulevardsecurity.securitymanagementapp.service.DisponibiliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@CrossOrigin(origins = "http:
@RequiredArgsConstructor
public class DisponibiliteController {

    private final DisponibiliteService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<DisponibiliteDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<DisponibiliteDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/agent/{agentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<DisponibiliteDto>> getByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(service.getByAgent(agentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<DisponibiliteDto> create(
            @RequestBody DisponibiliteCreationDto dto
    ) {
        DisponibiliteDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<DisponibiliteDto> update(
            @PathVariable Long id,
            @RequestBody DisponibiliteCreationDto dto
    ) {
        try {
            DisponibiliteDto updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}