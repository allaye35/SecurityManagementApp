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
     * ✅ Créer un nouveau pointage.
     * URL : POST /api/pointages/mission/{missionId}/agent/{agentId}
     *
     * Le JSON doit contenir :
     *  {
     *    "positionActuelle": { "latitude": 48.8566, "longitude": 2.3522 },
     *    "estPresent": true,
     *    "estRetard": false
     *  }
     */
    @PostMapping("/mission/{missionId}/agent/{agentId}")
    public ResponseEntity<?> creerPointage(
            @PathVariable Long missionId,
            @PathVariable Long agentId,
            @RequestBody Pointage pointage
    ) {
        try {
            // Enregistrement du pointage
            Pointage nouveauPointage = pointageService.creerPointage(missionId, agentId, pointage);
            return ResponseEntity.ok(nouveauPointage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * ✅ Récupérer tous les pointages
     * URL : GET /api/pointages
     */
    @GetMapping
    public ResponseEntity<List<Pointage>> obtenirTousLesPointages() {
        return ResponseEntity.ok(pointageService.obtenirTousLesPointages());
    }

    /**
     * ✅ Récupérer un pointage par ID
     * URL : GET /api/pointages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pointage> obtenirPointageParId(@PathVariable Long id) {
        Optional<Pointage> pointageOpt = pointageService.obtenirPointageParId(id);
        return pointageOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ✅ Récupérer tous les pointages d'une mission
     * URL : GET /api/pointages/mission/{missionId}
     */
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<Pointage>> obtenirPointagesParMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(pointageService.obtenirPointagesParMission(missionId));
    }

    /**
     * ✅ Mettre à jour un pointage existant
     * URL : PUT /api/pointages/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pointage> mettreAJourPointage(
            @PathVariable Long id,
            @RequestBody Pointage nouveauPointage
    ) {
        try {
            Pointage maj = pointageService.mettreAJourPointage(id, nouveauPointage);
            return ResponseEntity.ok(maj);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ✅ Supprimer un pointage par ID
     * URL : DELETE /api/pointages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPointage(@PathVariable Long id) {
        try {
            pointageService.supprimerPointage(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
