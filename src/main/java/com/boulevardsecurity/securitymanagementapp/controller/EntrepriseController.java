package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Entreprise;
import com.boulevardsecurity.securitymanagementapp.service.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")  // ✅ Vérifie bien l'URL de base
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    @Autowired
    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @GetMapping("/all")
    public List<Entreprise> getAllEntreprises() {
        return entrepriseService.getAllEntreprises();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getEntrepriseById(@PathVariable Long id) {
        Entreprise entreprise = entrepriseService.getEntrepriseById(id);
        return entreprise != null ? ResponseEntity.ok(entreprise) : ResponseEntity.notFound().build();
    }

    // ✅ Vérifie bien que l'annotation est correcte
    @PostMapping("/create")
    public ResponseEntity<Entreprise> createEntreprise(@RequestBody Entreprise entreprise) {
        Entreprise savedEntreprise = entrepriseService.createEntreprise(entreprise);
        return ResponseEntity.ok(savedEntreprise);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Entreprise> updateEntreprise(@PathVariable Long id, @RequestBody Entreprise updatedEntreprise) {
        Entreprise entreprise = entrepriseService.updateEntreprise(id, updatedEntreprise);
        return entreprise != null ? ResponseEntity.ok(entreprise) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        entrepriseService.deleteEntreprise(id);
        return ResponseEntity.noContent().build();
    }
}
