package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.FicheDePaie;
import com.boulevardsecurity.securitymanagementapp.service.FicheDePaieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fiches-de-paie")
public class FicheDePaieController {

    @Autowired
    private FicheDePaieService ficheDePaieService;

    @GetMapping
    public List<FicheDePaie> getAllFiches() {
        return ficheDePaieService.getAllFiches();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FicheDePaie> getFicheById(@PathVariable Long id) {
        Optional<FicheDePaie> fiche = ficheDePaieService.getFicheById(id);
        return fiche.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public FicheDePaie createFiche(@RequestBody FicheDePaie ficheDePaie) {
        return ficheDePaieService.createFiche(ficheDePaie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FicheDePaie> updateFiche(@PathVariable Long id, @RequestBody FicheDePaie updatedFiche) {
        FicheDePaie fiche = ficheDePaieService.updateFiche(id, updatedFiche);
        return fiche != null ? ResponseEntity.ok(fiche) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiche(@PathVariable Long id) {
        ficheDePaieService.deleteFiche(id);
        return ResponseEntity.noContent().build();
    }
}
