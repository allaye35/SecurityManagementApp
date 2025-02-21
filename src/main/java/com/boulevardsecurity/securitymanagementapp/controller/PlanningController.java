package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.service.PlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/plannings")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    // 🔹 Récupérer tous les plannings
    @GetMapping
    public List<Planning> getAllPlannings() {
        return planningService.getAllPlannings();
    }

    // 🔹 Récupérer un planning par ID
    @GetMapping("/{id}")
    public ResponseEntity<Planning> getPlanningById(@PathVariable Long id) {
        Optional<Planning> planning = planningService.getPlanningById(id);
        return planning.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter un planning
    @PostMapping
    public Planning createPlanning(@RequestBody Planning planning) {
        return planningService.savePlanning(planning);
    }

    // 🔹 Modifier un planning existant
    @PutMapping("/{id}")
    public ResponseEntity<Planning> updatePlanning(@PathVariable Long id, @RequestBody Planning updatedPlanning) {
        return planningService.getPlanningById(id).map(existingPlanning -> {
            existingPlanning.setDate(updatedPlanning.getDate());
            return ResponseEntity.ok(planningService.savePlanning(existingPlanning));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer un planning
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanning(@PathVariable Long id) {
        if (planningService.getPlanningById(id).isPresent()) {
            planningService.deletePlanning(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
