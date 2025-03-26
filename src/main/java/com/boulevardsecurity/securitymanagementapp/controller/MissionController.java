package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les Missions.
 * - Création (avec géocodage de l’adresse du site)
 * - Mise à jour (avec possibilité de re-géocoder)
 * - Affectation d’agents (avec notifications)
 * - Suppression, etc.
 *
 * Les notifications (emails/SMS) sont déclenchées au niveau du MissionService.
 */
@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer toutes les missions
    // ─────────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer une mission par ID
    // ─────────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        return missionService.getMissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Créer une nouvelle mission
    //   - mission : JSON avec titre, description, dates, etc.
    //   - adresseDuSite : paramètre optionnel (query param) pour géocoder
    // ─────────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Mission> createMission(
            @Valid @RequestBody Mission mission,
            @RequestParam(required = false) String adresseDuSite
    ) {
        try {
            Mission created = missionService.saveMission(mission, adresseDuSite);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            // En cas d’erreur dans le géocodage ou autre
            return ResponseEntity.badRequest().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Mettre à jour une mission existante
    //   - possible re-géocodage si on fournit "nouvelleAdresse"
    // ─────────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Mission> updateMission(
            @PathVariable Long id,
            @RequestBody Mission updatedMission,
            @RequestParam(required = false) String nouvelleAdresse
    ) {
        try {
            Mission updated = missionService.updateMission(id, updatedMission, nouvelleAdresse);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Supprimer une mission
    // ─────────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        Optional<Mission> existing = missionService.getMissionById(id);
        if (existing.isPresent()) {
            missionService.deleteMission(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Affecter plusieurs agents à une mission
    //   - La liste agentIds contient les IDs des agents à affecter
    // ─────────────────────────────────────────────────────────────────────────────
    // Pour ajouter un seul agent
    @PutMapping("/{missionId}/agent")
    public ResponseEntity<?> assignSingleAgentToMission(
            @PathVariable Long missionId,
            @RequestBody Long agentId
    ) {
        try {
            return ResponseEntity.ok(missionService.assignAgents(missionId, List.of(agentId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Pour ajouter une liste d'agents
    @PutMapping("/{missionId}/agents")
    public ResponseEntity<?> assignAgentsToMission(
            @PathVariable Long missionId,
            @RequestBody List<Long> agentIds
    ) {
        try {
            return ResponseEntity.ok(missionService.assignAgents(missionId, agentIds));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Empêcher la rédaction d’un rapport avant le début de la mission
    // ─────────────────────────────────────────────────────────────────────────────
    @PutMapping("/{missionId}/rapport/{rapportId}")
    public ResponseEntity<?> assignRapportToMission(
            @PathVariable Long missionId,
            @PathVariable Long rapportId
    ) {
        try {
            return ResponseEntity.ok(missionService.assignRapport(missionId, rapportId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer un planning à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    @PutMapping("/{missionId}/planning/{planningId}")
    public ResponseEntity<Mission> assignPlanningToMission(
            @PathVariable Long missionId,
            @PathVariable Long planningId
    ) {
        return missionService.assignPlanning(missionId, planningId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer un site à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    @PutMapping("/{missionId}/site/{siteId}")
    public ResponseEntity<Mission> assignSiteToMission(
            @PathVariable Long missionId,
            @PathVariable Long siteId
    ) {
        return missionService.assignSite(missionId, siteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer une entreprise à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    @PutMapping("/{missionId}/entreprise/{entrepriseId}")
    public ResponseEntity<Mission> assignEntrepriseToMission(
            @PathVariable Long missionId,
            @PathVariable Long entrepriseId
    ) {
        return missionService.assignEntreprise(missionId, entrepriseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer une géolocalisation déjà existante à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    @PutMapping("/{missionId}/geolocalisation")
    public ResponseEntity<Mission> assignGeolocalisationToMission(
            @PathVariable Long missionId
    ) {
        return missionService.assignGeolocalisationToMission(missionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer les missions qui commencent après une date donnée
    // ─────────────────────────────────────────────────────────────────────────────
    @GetMapping("/after/{date}")
    public ResponseEntity<List<Mission>> getMissionsStartingAfter(@PathVariable String date) {
        return ResponseEntity.ok(missionService.getMissionsStartingAfter(LocalDate.parse(date)));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer les missions qui terminent avant une date donnée
    // ─────────────────────────────────────────────────────────────────────────────
    @GetMapping("/before/{date}")
    public ResponseEntity<List<Mission>> getMissionsEndingBefore(@PathVariable String date) {
        return ResponseEntity.ok(missionService.getMissionsEndingBefore(LocalDate.parse(date)));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer toutes les missions d'un agent
    // ─────────────────────────────────────────────────────────────────────────────
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Mission>> getMissionsByAgentId(@PathVariable Long agentId) {
        return ResponseEntity.ok(missionService.getMissionsByAgentId(agentId));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer toutes les missions d'un planning
    // ─────────────────────────────────────────────────────────────────────────────
    @GetMapping("/planning/{planningId}")
    public ResponseEntity<List<Mission>> getMissionsByPlanningId(@PathVariable Long planningId) {
        return ResponseEntity.ok(missionService.getMissionsByPlanningId(planningId));
    }
}
