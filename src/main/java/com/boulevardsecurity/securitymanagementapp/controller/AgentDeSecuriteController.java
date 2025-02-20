package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")  // Autoriser toutes les requêtes externes
@RestController
@RequestMapping("/api/agents")
public class AgentDeSecuriteController {

    private final AgentDeSecuriteService agentDeSecuriteService;

    @Autowired
    AgentDeSecuriteController(AgentDeSecuriteService agentDeSecuriteService) {
        this.agentDeSecuriteService = agentDeSecuriteService;
    }

    //  Récupérer tous les agents
    //  URL : GET http://localhost:8080/api/agents
    @GetMapping("/")
    public List<AgentDeSecurite> getAllAgents() {
        return agentDeSecuriteService.getAllAgents();
    }

    //  Récupérer un agent par ID
    //  URL : GET http://localhost:8080/api/agents/1
    @GetMapping("/id/{id}")
    public ResponseEntity<AgentDeSecurite> getAgentById(@PathVariable Long id) {
        AgentDeSecurite agent = agentDeSecuriteService.getAgentById(id);
        return (agent != null) ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
    }

    // Créer un nouvel agent
    @PostMapping("/create")
    public ResponseEntity<AgentDeSecurite> createAgent(@RequestBody AgentDeSecurite agent) {
        AgentDeSecurite newAgent = agentDeSecuriteService.createAgent(agent);
        return ResponseEntity.ok(newAgent);
    }


    //  Mettre à jour un agent par ID
    // URL : PUT http://localhost:8080/api/agents/update/1
    //  Body (JSON)
    @PutMapping("/update/{id}")
    public ResponseEntity<AgentDeSecurite> updateAgent(@PathVariable Long id, @RequestBody AgentDeSecurite updatedAgent) {
        AgentDeSecurite agent = agentDeSecuriteService.updateAgent(id, updatedAgent);
        System.out.println(agent);
        return agent != null ? ResponseEntity.ok(agent) : ResponseEntity.notFound().build();
    }





    //  Supprimer un agent par ID
    //  URL : DELETE http://localhost:8080/api/agents/delete/1
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        boolean deleted = agentDeSecuriteService.deleteAgent(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
