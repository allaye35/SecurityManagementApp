package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentService;

    @Autowired
    public AgentDeSecuriteController(AgentDeSecuriteService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/listes")
    public List<AgentDeSecurite> getAllAgents() {
        return agentService.getAllAgents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentDeSecurite> getAgentById(@PathVariable Long id) {
        AgentDeSecurite agent = agentService.getAgentById(id);
        return agent != null ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
    }

    @PostMapping("/ajouter")
    public ResponseEntity<AgentDeSecurite> createAgent(@RequestBody AgentDeSecurite agent) {
        AgentDeSecurite newAgent = agentService.createAgent(agent);
        return ResponseEntity.ok(newAgent);
    }

    @PutMapping("/modifier/{id}")
    public ResponseEntity<AgentDeSecurite> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecurite updatedAgent) {
        AgentDeSecurite agent = agentService.updateAgent(id, updatedAgent);
        return agent != null ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
}
