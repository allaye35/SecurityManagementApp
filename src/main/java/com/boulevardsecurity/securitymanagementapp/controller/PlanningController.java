package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.service.PlanningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plannings")
public class PlanningController {

    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    // ✅ Ajouter un agent
    @PostMapping("/{id}/agents")
    public ResponseEntity<Planning> ajouterAgent(@PathVariable Long id, @RequestBody AgentDeSecurite agent) {
        return ResponseEntity.ok(planningService.ajouterAgent(id, agent));
    }

    // ✅ Supprimer un agent
    @DeleteMapping("/{id}/agents")
    public ResponseEntity<Planning> supprimerAgent(@PathVariable Long id, @RequestBody AgentDeSecurite agent) {
        return ResponseEntity.ok(planningService.supprimerAgent(id, agent));
    }

    // ✅ Ajouter une mission
    @PostMapping("/{id}/missions")
    public ResponseEntity<Planning> ajouterMission(@PathVariable Long id, @RequestBody Mission mission) {
        return ResponseEntity.ok(planningService.ajouterMission(id, mission));
    }

    // ✅ Supprimer une mission
    @DeleteMapping("/{id}/missions")
    public ResponseEntity<Planning> supprimerMission(@PathVariable Long id, @RequestBody Mission mission) {
        return ResponseEntity.ok(planningService.supprimerMission(id, mission));
    }

    // ✅ Récupérer le nombre d'agents
    @GetMapping("/{id}/nombre-agents")
    public ResponseEntity<Integer> getNombreAgents(@PathVariable Long id) {
        return ResponseEntity.ok(planningService.getNombreAgents(id));
    }

    // ✅ Récupérer le nombre de missions
    @GetMapping("/{id}/nombre-missions")
    public ResponseEntity<Integer> getNombreMissions(@PathVariable Long id) {
        return ResponseEntity.ok(planningService.getNombreMissions(id));
    }
}
