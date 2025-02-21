package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgentDeSecuriteService {

    private final AgentDeSecuriteRepository agentRepository;

    // 🔹 Récupérer tous les agents
    public List<AgentDeSecurite> getAllAgents() {
        return agentRepository.findAll();
    }

    // 🔹 Récupérer un agent par ID
    public Optional<AgentDeSecurite> getAgentById(Long id) {
        return agentRepository.findById(id);
    }

    // 🔹 Ajouter ou mettre à jour un agent
    public AgentDeSecurite saveAgent(AgentDeSecurite agent) {
        return agentRepository.save(agent);
    }

    // 🔹 Supprimer un agent par ID
    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }
}
