package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agents") // URL principale de ton API
@CrossOrigin(origins = "http://localhost:3000") // Permet les requêtes depuis ton application React sur le port 3000
@RequiredArgsConstructor
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentDeSecuriteService;

    // 🔹 Récupérer tous les agents
    @GetMapping
    public ResponseEntity<List<AgentDeSecurite>> getAllAgents() {
        List<AgentDeSecurite> agents = agentDeSecuriteService.getAllAgents();
        return ResponseEntity.ok(agents);
    }

    // 🔹 Récupérer un agent par ID
    @GetMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> getAgentById(@PathVariable Long id) {
        Optional<AgentDeSecurite> agent = agentDeSecuriteService.getAgentById(id);
        return agent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter un nouvel agent
    @PostMapping
    public ResponseEntity<AgentDeSecurite> createAgent(@RequestBody AgentDeSecurite agent) {
        try {
            AgentDeSecurite savedAgent = agentDeSecuriteService.saveAgent(agent);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAgent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔹 Modifier un agent existant
    @PutMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecurite updatedAgent) {
        try {
            AgentDeSecurite updated = agentDeSecuriteService.updateAgent(id, updatedAgent);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 🔹 Supprimer un agent par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        try {
            agentDeSecuriteService.deleteAgent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 🔹 Permettre à un agent de consulter son planning via ses missions
    @GetMapping("/{agentId}/planning")
    public ResponseEntity<Planning> getPlanningByAgentId(@PathVariable Long agentId) {
        return agentDeSecuriteService. getPlanningByAgentId(agentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Assigner une zone de travail à un agent
    @PutMapping("/{agentId}/zone/{zoneId}")
    public ResponseEntity<AgentDeSecurite> assignerZoneDeTravail(@PathVariable Long agentId, @PathVariable Long zoneId) {
        try {
            AgentDeSecurite updatedAgent = agentDeSecuriteService.assignerZoneDeTravail(agentId, zoneId);
            return ResponseEntity.ok(updatedAgent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

