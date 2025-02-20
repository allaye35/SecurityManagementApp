package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.ContratDePrestationDeService;
import com.boulevardsecurity.securitymanagementapp.service.ContratDePrestationDeServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats-prestation")
@RequiredArgsConstructor
public class ContratDePrestationDeServiceController {

    private final ContratDePrestationDeServiceService service;

    @GetMapping
    public ResponseEntity<List<ContratDePrestationDeService>> getAllContrats() {
        return ResponseEntity.ok(service.getAllContrats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratDePrestationDeService> getContratById(@PathVariable Long id) {
        Optional<ContratDePrestationDeService> contrat = service.getContratById(id);
        return contrat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContratDePrestationDeService> createContrat(@RequestBody ContratDePrestationDeService contrat) {
        return ResponseEntity.ok(service.createContrat(contrat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratDePrestationDeService> updateContrat(@PathVariable Long id, @RequestBody ContratDePrestationDeService updatedContrat) {
        ContratDePrestationDeService contrat = service.updateContrat(id, updatedContrat);
        return (contrat != null) ? ResponseEntity.ok(contrat) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        service.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }
}
