
package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.*;
import com.boulevardsecurity.securitymanagementapp.service.ContratService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ContratController {

    private final ContratService service;    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ContratCreateDto dto) {
        try {
            ContratDto created = service.createContrat(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage(), "status", "error"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la création du contrat: " + e.getMessage(), 
                            "status", "error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratDto> update(@PathVariable Long id, @RequestBody ContratCreateDto dto) {
        return ResponseEntity.ok(service.updateContrat(id, dto));
    }

    @GetMapping
    public List<ContratDto> getAll() {
        return service.getAllContrats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratDto> getById(@PathVariable Long id) {
        return service.getContratById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ref/{ref}")
    public ResponseEntity<ContratDto> getByRef(@PathVariable String ref) {
        return service.getContratByReference(ref).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/devis/{devisId}")
    public ResponseEntity<ContratDto> getByDevisId(@PathVariable Long devisId) {
        return service.getContratByDevisId(devisId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }
}

