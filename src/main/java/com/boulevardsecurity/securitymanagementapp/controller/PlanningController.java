package com.boulevardsecurity.securitymanagementapp.controller;


import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.service.PlanningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plannings")
public class PlanningController {

    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @GetMapping("/all")
    public List<Planning> getAllPlannings() {
        return planningService.getAllPlannings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planning> getPlanningById(@PathVariable Long id) {
        Planning planning = planningService.getPlanningById(id);
        return planning != null ? ResponseEntity.ok(planning) : ResponseEntity.notFound().build();
    }

    @PostMapping("/ajouter")
    public Planning createPlanning(@RequestBody Planning planning) {
        return planningService.createPlanning(planning);
    }

    @PutMapping("/modifier/{id}")
    public ResponseEntity<Planning> updatePlanning(@PathVariable Long id, @RequestBody Planning updatedPlanning) {
        Planning planning = planningService.updatePlanning(id, updatedPlanning);
        return planning != null ? ResponseEntity.ok(planning) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> deletePlanning(@PathVariable Long id) {
        planningService.deletePlanning(id);
        return ResponseEntity.noContent().build();
    }
}
