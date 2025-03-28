package com.boulevardsecurity.securitymanagementapp.controller;


import com.boulevardsecurity.securitymanagementapp.model.Disponibilite;
import com.boulevardsecurity.securitymanagementapp.service.DisponibiliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disponibilites")
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @Autowired
    public DisponibiliteController(DisponibiliteService disponibiliteService) {
        this.disponibiliteService = disponibiliteService;
    }

    //  Créer une nouvelle disponibilité
    @PostMapping
    public ResponseEntity<Disponibilite> creerDisponibilite(@RequestBody Disponibilite disponibilite) {
        Disponibilite nouvelleDisponibilite = disponibiliteService.creerDisponibilite(disponibilite);
        return ResponseEntity.ok(nouvelleDisponibilite);
    }

    //  Obtenir toutes les disponibilités
    @GetMapping
    public ResponseEntity<List<Disponibilite>> obtenirToutesLesDisponibilites() {
        List<Disponibilite> disponibilites = disponibiliteService.obtenirToutesLesDisponibilites();
        return ResponseEntity.ok(disponibilites);
    }

    //  Obtenir une disponibilité par ID
    @GetMapping("/{id}")
    public ResponseEntity<Disponibilite> obtenirDisponibiliteParId(@PathVariable int id) {
        Optional<Disponibilite> disponibilite = disponibiliteService.obtenirDisponibiliteParId(id);

        return disponibilite.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Obtenir toutes les disponibilités d'un agent par son ID
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Disponibilite>> obtenirDisponibilitesParAgent(@PathVariable Long agentId) {
        List<Disponibilite> disponibilites = disponibiliteService.obtenirDisponibilitesParAgent(agentId);
        return ResponseEntity.ok(disponibilites);
    }

    //  Mettre à jour une disponibilité par ID
    @PutMapping("/{id}")
    public ResponseEntity<Disponibilite> mettreAJourDisponibilite(@PathVariable int id, @RequestBody Disponibilite nouvelleDisponibilite) {
        try {
            Disponibilite disponibiliteMiseAJour = disponibiliteService.mettreAJourDisponibilite(id, nouvelleDisponibilite);
            return ResponseEntity.ok(disponibiliteMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //  Supprimer une disponibilité par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerDisponibilite(@PathVariable int id) {
        try {
            disponibiliteService.supprimerDisponibilite(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }




}

