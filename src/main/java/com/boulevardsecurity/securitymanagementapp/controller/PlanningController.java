package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.service.PlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/plannings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PlanningController {

    private final PlanningService planningService;

    // 🔹 Obtenir tous les plannings
    @GetMapping
    public List<Planning> getAllPlannings() {
        return planningService.getAllPlannings();
    }

    // 🔹 Obtenir un planning par ID
    @GetMapping("/{id}")
    public ResponseEntity<Planning> getPlanningById(@PathVariable Long id) {
        return planningService.getPlanningById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Créer un nouveau planning
    @PostMapping
    public Planning createPlanning(@RequestBody Planning planning) {
        return planningService.savePlanning(planning);
    }

    // 🔹 Modifier un planning existant
    @PutMapping("/{id}")
    public ResponseEntity<Planning> updatePlanning(@PathVariable Long id, @RequestBody Planning planning) {
        planning.setId(id);
        return ResponseEntity.ok(planningService.savePlanning(planning));
    }

    // 🔹 Supprimer un planning
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanning(@PathVariable Long id) {
        planningService.deletePlanning(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Ajouter une mission au planning
    @PostMapping("/{planningId}/missions/{missionId}")
    public Planning ajouterMission(@PathVariable Long planningId, @PathVariable Long missionId) {
        return planningService.ajouterMissionAuPlanning(planningId, missionId);
    }

    // 🔹 Supprimer une mission du planning
    @DeleteMapping("/{planningId}/missions/{missionId}")
    public ResponseEntity<Void> retirerMissionDuPlanning(@PathVariable Long planningId, @PathVariable Long missionId) {
        try {
            planningService.retirerMissionDuPlanning(planningId, missionId);
            return ResponseEntity.noContent().build(); // ✅ HTTP 204 No Content (succès sans retour)
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // ✅ HTTP 400 si erreur
        }
    }



    // 🔹 Filtrer les plannings par date
    @GetMapping("/rechercher")
    public ResponseEntity<List<Planning>> getPlanningsByDateRange(
            @RequestParam("dateDebut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        List<Planning> plannings = planningService.getPlanningsByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(plannings);
    }



    // 🔹 Récupérer les plannings d'un agent
    @GetMapping("/agents/{agentId}")
    public ResponseEntity<List<Planning>> getPlanningsByAgent(@PathVariable Long agentId) {
        List<Planning> plannings = planningService.getPlanningsByAgent(agentId);
        return ResponseEntity.ok(plannings);
    }

    // 🔹 Récupérer les plannings d'une mission
    @GetMapping("/missions/{missionId}")
    public ResponseEntity<List<Planning>> getPlanningsByMission(@PathVariable Long missionId) {
        List<Planning> plannings = planningService.getPlanningsByMission(missionId);
        return ResponseEntity.ok(plannings);
    }
}
