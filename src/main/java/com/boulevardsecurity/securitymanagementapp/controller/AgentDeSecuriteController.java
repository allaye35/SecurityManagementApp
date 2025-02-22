package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agents") // <== L'URL doit être bien définie ici
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentDeSecuriteService;

    @Autowired
    public AgentDeSecuriteController(AgentDeSecuriteService agentDeSecuriteService) {
        this.agentDeSecuriteService = agentDeSecuriteService;
    }
    // 🔹 Récupérer tous les agents
    @GetMapping
    public List<AgentDeSecurite> getAllAgents() {
        return agentDeSecuriteService.getAllAgents();
    }

    // 🔹 Récupérer un agent par ID
    @GetMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> getAgentById(@PathVariable Long id) {
        Optional<AgentDeSecurite> agent = agentDeSecuriteService.getAgentById(id);
        return agent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter un agent
    @PostMapping
    public AgentDeSecurite createAgent(@RequestBody AgentDeSecurite agent) {
        return agentDeSecuriteService.saveAgent(agent);
    }

    // 🔹 Modifier un agent existant
    @PutMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecurite updatedAgent) {
        return agentDeSecuriteService.getAgentById(id).map(existingAgent -> {
            existingAgent.setNom(updatedAgent.getNom());
            existingAgent.setPrenom(updatedAgent.getPrenom());
            existingAgent.setEmail(updatedAgent.getEmail());
            existingAgent.setTelephone(updatedAgent.getTelephone());
            existingAgent.setAdresse(updatedAgent.getAdresse());
            existingAgent.setDateNaissance(updatedAgent.getDateNaissance());
            existingAgent.setZoneDeTravail(updatedAgent.getZoneDeTravail());
            existingAgent.setStatut(updatedAgent.getStatut());
            return ResponseEntity.ok(agentDeSecuriteService.saveAgent(existingAgent));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer un agent
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        if (agentDeSecuriteService.getAgentById(id).isPresent()) {
            agentDeSecuriteService.deleteAgent(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 🔹 Permettre à un agent de consulter son planning via ses missions
    @GetMapping("/{agentId}/planning")
    public ResponseEntity<Planning> getPlanningByAgentId(@PathVariable Long agentId) {
        return agentDeSecuriteService.getPlanningByAgentId(agentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
