package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Contrat;
import com.boulevardsecurity.securitymanagementapp.model.Devis;
import com.boulevardsecurity.securitymanagementapp.service.ContratService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des Contrats (CRUD).
 */
@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final ContratService contratService;

    /**
     * CREATE
     * POST /api/contrats
     */
    @PostMapping
    public ResponseEntity<Contrat> createContrat(@RequestBody Contrat contrat) {
        Contrat newContrat = contratService.createContrat(contrat);
        return ResponseEntity.status(201).body(newContrat);
    }

    /**
     * READ - un Contrat
     * GET /api/contrats/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contrat> getContrat(@PathVariable Long id) {
        Contrat foundContrat = contratService.getContratById(id);
        return ResponseEntity.ok(foundContrat);
    }

    /**
     * READ - Liste de Contrats
     * GET /api/contrats
     */
    @GetMapping
    public List<Contrat> getAllContrats() {
        return contratService.getAllContrats();
    }

    /**
     * UPDATE
     * PUT /api/contrats/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contrat> updateContrat(
            @PathVariable Long id,
            @RequestBody Contrat contratData
    ) {
        Contrat updatedContrat = contratService.updateContrat(id, contratData);
        return ResponseEntity.ok(updatedContrat);
    }

    /**
     * DELETE
     * DELETE /api/contrats/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        contratService.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * (Optionnel) Génére un Contrat à partir d'un Devis.
     * POST /api/contrats/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<Contrat> generateContratFromDevis(@RequestBody Devis devis) {
        Contrat newContrat = contratService.generateFromDevis(devis);
        return ResponseEntity.status(201).body(newContrat);
    }
}
