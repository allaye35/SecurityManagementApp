package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentService;

    // 🔹 Récupérer tous les agents
    @GetMapping
    public List<AgentDeSecurite> getAllAgents() {
        return agentService.getAllAgents();
    }

    // 🔹 Récupérer un agent par ID
    @GetMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> getAgentById(@PathVariable Long id) {
        Optional<AgentDeSecurite> agent = agentService.getAgentById(id);
        return agent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter un agent
    @PostMapping
    public AgentDeSecurite createAgent(@RequestBody AgentDeSecurite agent) {
        return agentService.saveAgent(agent);
    }

    // 🔹 Modifier un agent existant
    @PutMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecurite updatedAgent) {
        return agentService.getAgentById(id).map(existingAgent -> {
            existingAgent.setNom(updatedAgent.getNom());
            existingAgent.setPrenom(updatedAgent.getPrenom());
            existingAgent.setEmail(updatedAgent.getEmail());
            existingAgent.setTelephone(updatedAgent.getTelephone());
            existingAgent.setAdresse(updatedAgent.getAdresse());
            existingAgent.setDateNaissance(updatedAgent.getDateNaissance());
            existingAgent.setZoneDeTravail(updatedAgent.getZoneDeTravail());
            existingAgent.setStatut(updatedAgent.getStatut());
            return ResponseEntity.ok(agentService.saveAgent(existingAgent));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer un agent
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        if (agentService.getAgentById(id).isPresent()) {
            agentService.deleteAgent(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
