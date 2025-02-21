package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Entreprise;
import com.boulevardsecurity.securitymanagementapp.service.EntrepriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/entreprises")
@RequiredArgsConstructor
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    // 🔹 Récupérer toutes les entreprises
    @GetMapping
    public List<Entreprise> getAllEntreprises() {
        return entrepriseService.getAllEntreprises();
    }

    // 🔹 Récupérer une entreprise par ID
    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getEntrepriseById(@PathVariable Long id) {
        Optional<Entreprise> entreprise = entrepriseService.getEntrepriseById(id);
        return entreprise.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter une entreprise
    @PostMapping
    public Entreprise createEntreprise(@RequestBody Entreprise entreprise) {
        return entrepriseService.saveEntreprise(entreprise);
    }

    // 🔹 Modifier une entreprise existante
    @PutMapping("/{id}")
    public ResponseEntity<Entreprise> updateEntreprise(@PathVariable Long id, @RequestBody Entreprise updatedEntreprise) {
        return entrepriseService.getEntrepriseById(id).map(existingEntreprise -> {
            existingEntreprise.setNom(updatedEntreprise.getNom());
            existingEntreprise.setAdresse(updatedEntreprise.getAdresse());
            existingEntreprise.setTelephone(updatedEntreprise.getTelephone());
            return ResponseEntity.ok(entrepriseService.saveEntreprise(existingEntreprise));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer une entreprise
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        if (entrepriseService.getEntrepriseById(id).isPresent()) {
            entrepriseService.deleteEntreprise(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
