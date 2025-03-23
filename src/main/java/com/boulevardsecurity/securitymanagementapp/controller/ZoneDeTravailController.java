package com.boulevardsecurity.securitymanagementapp.controller;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeZone;
import com.boulevardsecurity.securitymanagementapp.model.ZoneDeTravail;
import com.boulevardsecurity.securitymanagementapp.service.ZoneDeTravailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneDeTravailController {

    @Autowired
    private ZoneDeTravailService zoneService;

    // Créer une nouvelle zone de travail
    @PostMapping
    public ResponseEntity<ZoneDeTravail> creerZoneDeTravail(@RequestBody ZoneDeTravail zone) {
        try {
            return ResponseEntity.ok(zoneService.creerZoneDeTravail(zone));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //  Récupérer toutes les zones de travail
    @GetMapping
    public ResponseEntity<List<ZoneDeTravail>> obtenirToutesLesZones() {
        return ResponseEntity.ok(zoneService.obtenirToutesLesZones());
    }

    //  Récupérer une zone de travail par ID
    @GetMapping("/{id}")
    public ResponseEntity<ZoneDeTravail> obtenirZoneParId(@PathVariable Long id) {
        return zoneService.obtenirZoneParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Rechercher des zones par nom
    @GetMapping("/recherche")
    public ResponseEntity<List<ZoneDeTravail>> rechercherZonesParNom(@RequestParam String nom) {
        return ResponseEntity.ok(zoneService.rechercherZonesParNom(nom));
    }

    // Rechercher des zones par type
    @GetMapping("/type/{typeZone}")
    public ResponseEntity<List<ZoneDeTravail>> rechercherZonesParType(@PathVariable TypeZone typeZone) {
        return ResponseEntity.ok(zoneService.rechercherZonesParType(typeZone));
    }

    //  Mettre à jour une zone de travail
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDeTravail> mettreAJourZoneDeTravail(@PathVariable Long id, @RequestBody ZoneDeTravail zone) {
        try {
            return ResponseEntity.ok(zoneService.mettreAJourZoneDeTravail(id, zone));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //  Supprimer une zone de travail par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerZoneDeTravail(@PathVariable Long id) {
        try {
            zoneService.supprimerZoneDeTravail(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

