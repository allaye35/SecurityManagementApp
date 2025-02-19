package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import java.util.List;
import java.util.Optional;

@Service
public class AgentDeSecuriteService {

    private final AgentDeSecuriteRepository agentDeSecuriteRepository;

    @Autowired
    public AgentDeSecuriteService(AgentDeSecuriteRepository agentDeSecuriteRepository) {
        this.agentDeSecuriteRepository = agentDeSecuriteRepository;
    }

    // Récupérer tous les agents de sécurité
    public List<AgentDeSecurite> getAllAgents() {
        return agentDeSecuriteRepository.findAll();
    }

    // Récupérer un agent par son ID
    public AgentDeSecurite getAgentById(Long id) {
        return agentDeSecuriteRepository.findById(id).orElse(null);
    }

    // Ajouter un nouvel agent de sécurité
    public AgentDeSecurite createAgent(AgentDeSecurite agent) {
        return agentDeSecuriteRepository.save(agent);
    }

    // Mettre à jour un agent de sécurité existant
    public AgentDeSecurite updateAgent(Long id, AgentDeSecurite updatedAgent) {
        Optional<AgentDeSecurite> existingAgent = agentDeSecuriteRepository.findById(id);
        if (existingAgent.isPresent()) {
            AgentDeSecurite agent = existingAgent.get();
            agent.setNom(updatedAgent.getNom());
            agent.setPrenom(updatedAgent.getPrenom());
            agent.setTelephone(updatedAgent.getTelephone());
            agent.setEmail(updatedAgent.getEmail());
            agent.setAdresse(updatedAgent.getAdresse());
            return agentDeSecuriteRepository.save(agent);
        }
        return null; // ou lever une exception
    }

    // Supprimer un agent de sécurité par son ID
    public boolean deleteAgent(Long id) {
        if (agentDeSecuriteRepository.existsById(id)) {
            agentDeSecuriteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
