package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentDeSecuriteService;

    @Autowired
    AgentDeSecuriteController(AgentDeSecuriteService agentDeSecuriteService) {
        this.agentDeSecuriteService = agentDeSecuriteService;
    }

    // Récupérer tous les agents
    @GetMapping("/")
    public List<AgentDeSecurite> getAllAgents() {
        return agentDeSecuriteService.getAllAgents();
    }

    // Récupérer un agent par ID
    @GetMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> getAgentById(@PathVariable Long id) {
        AgentDeSecurite agent = agentDeSecuriteService.getAgentById(id);
        return (agent != null) ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
    }

    // Créer un nouvel agent
    @PostMapping
    public AgentDeSecurite createAgent(@RequestBody AgentDeSecurite agent) {
        return agentDeSecuriteService.createAgent(agent);
    }

    // Mettre à jour un agent
    @PutMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecurite updatedAgent) {
        AgentDeSecurite agent = agentDeSecuriteService.updateAgent(id, updatedAgent);
        return (agent != null) ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
    }

    // Supprimer un agent
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        return agentDeSecuriteService.deleteAgent(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
