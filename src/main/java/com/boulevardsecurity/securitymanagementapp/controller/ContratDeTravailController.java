package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import com.boulevardsecurity.securitymanagementapp.service.ContratDeTravailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats")
public class ContratDeTravailController {

    private final ContratDeTravailService contratService;

    @Autowired
    public ContratDeTravailController(ContratDeTravailService contratService) {
        this.contratService = contratService;
    }

    @GetMapping
    public List<ContratDeTravail> getAllContrats() {
        return contratService.getAllContrats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratDeTravail> getContratById(@PathVariable Long id) {
        Optional<ContratDeTravail> contrat = contratService.getContratById(id);
        return contrat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContratDeTravail> createContrat(@RequestBody ContratDeTravail contrat) {
        return ResponseEntity.ok(contratService.createContrat(contrat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratDeTravail> updateContrat(@PathVariable Long id, @RequestBody ContratDeTravail updatedContrat) {
        ContratDeTravail contrat = contratService.updateContrat(id, updatedContrat);
        return contrat != null ? ResponseEntity.ok(contrat) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        contratService.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/prolonger")
    public ResponseEntity<String> prolongerContrat(@PathVariable Long id, @RequestParam LocalDate nouvelleDateFin) {
        boolean success = contratService.prolongerContrat(id, nouvelleDateFin);
        return success ? ResponseEntity.ok("Contrat prolongé avec succès") : ResponseEntity.badRequest().body("Erreur de prolongation");
    }
}
