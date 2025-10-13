package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.ClientCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.ClientDto;
import com.boulevardsecurity.securitymanagementapp.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http:
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

@PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientDto> create(@RequestBody ClientCreateDto dto) {
        ClientDto created = service.createClient(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<List<ClientDto>> getAll() {
        return ResponseEntity.ok(service.getAllClients());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ClientDto> getById(@PathVariable Long id) {
        return service.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ClientDto> getByEmail(@PathVariable String email) {
        return service.getClientByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nom/{nom}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ClientDto> getByNom(@PathVariable String nom) {
        return service.getClientByNom(nom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientDto dto) {
        ClientDto updated = service.updateClient(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientDto> updateRole(@PathVariable Long id, @RequestBody Map<String, String> roleData) {
        String newRole = roleData.get("role");
        
        if (newRole == null || (!newRole.equals("ADMIN") && !newRole.equals("CLIENT") && !newRole.equals("AGENT_SECURITE"))) {
            return ResponseEntity.badRequest().build();
        }
        
        ClientDto updated = service.updateClientRole(id, newRole);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ClientDto>> listPending() {
        return ResponseEntity.ok(service.getPendingApprovalClients());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approveClient(id));
    }
}