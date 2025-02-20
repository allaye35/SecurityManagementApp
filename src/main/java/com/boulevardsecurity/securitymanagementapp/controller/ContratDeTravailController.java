package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import com.boulevardsecurity.securitymanagementapp.service.ContratDeTravailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats")
public class ContratDeTravailController {

    private final ContratDeTravailService contratDeTravailService;

    @Autowired
    public ContratDeTravailController(ContratDeTravailService contratDeTravailService) {
        this.contratDeTravailService = contratDeTravailService;
    }

    @GetMapping("/all")
    public List<ContratDeTravail> getAllContrats() {
        return contratDeTravailService.getAllContrats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratDeTravail> getContratById(@PathVariable Long id) {
        Optional<ContratDeTravail> contrat = contratDeTravailService.getContratById(id);
        return contrat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/ajouter")
    public ResponseEntity<ContratDeTravail> createContrat(@RequestBody ContratDeTravail contratDeTravail) {
        return ResponseEntity.ok(contratDeTravailService.createContrat(contratDeTravail));
    }

    @PutMapping("/modifier/{id}")
    public ResponseEntity<ContratDeTravail> updateContrat(@PathVariable Long id, @RequestBody ContratDeTravail updatedContrat) {
        ContratDeTravail contrat = contratDeTravailService.updateContrat(id, updatedContrat);
        return contrat != null ? ResponseEntity.ok(contrat) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        contratDeTravailService.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }
}
