package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Pointage;
import com.boulevardsecurity.securitymanagementapp.service.PointageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur permettant la création et la gestion des pointages
 */
@RestController
@RequestMapping("/api/pointages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PointageController {

    private final PointageService pointageService;

    /**
     * Créer un nouveau pointage.
     * Le JSON doit contenir :
     *  {
     *    "mission": { "id": 1 },
     *    "positionActuelle": { "latitude": 48.8566, "longitude": 2.3522 },
     *    "estPresent": true,
     *    "estRetard": false
     *  }
     */
    @PostMapping
    public ResponseEntity<?> creerPointage(@RequestBody Pointage pointage) {
        try {
            Pointage nouveauPointage = pointageService.creerPointage(pointage);
            return ResponseEntity.ok(nouveauPointage);
        } catch (IllegalArgumentException e) {
            // ex: si l'agent n'est pas dans la zone
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // ex: mission introuvable, etc.
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer tous les pointages
     */
    @GetMapping
    public ResponseEntity<List<Pointage>> obtenirTousLesPointages() {
        return ResponseEntity.ok(pointageService.obtenirTousLesPointages());
    }

    /**
     * Récupérer un pointage par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pointage> obtenirPointageParId(@PathVariable Long id) {
        Optional<Pointage> pointageOpt = pointageService.obtenirPointageParId(id);
        return pointageOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupérer tous les pointages d'une mission
     */
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<Pointage>> obtenirPointagesParMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(pointageService.obtenirPointagesParMission(missionId));
    }

    /**
     * Mettre à jour un pointage existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pointage> mettreAJourPointage(@PathVariable Long id,
                                                        @RequestBody Pointage nouveauPointage) {
        try {
            Pointage maj = pointageService.mettreAJourPointage(id, nouveauPointage);
            return ResponseEntity.ok(maj);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un pointage par ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPointage(@PathVariable Long id) {
        try {
            pointageService.supprimerPointage(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
