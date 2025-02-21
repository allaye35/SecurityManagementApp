package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgentDeSecuriteService {

    private final AgentDeSecuriteRepository agentRepository;

    @Autowired
    public AgentDeSecuriteService(AgentDeSecuriteRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<AgentDeSecurite> getAllAgents() {
        return agentRepository.findAll();
    }

    public AgentDeSecurite getAgentById(Long id) {
        return agentRepository.findById(id).orElse(null);
    }

    public AgentDeSecurite createAgent(AgentDeSecurite agent) {
        return agentRepository.save(agent);
    }

    public AgentDeSecurite updateAgent(Long id, AgentDeSecurite updatedAgent) {
        Optional<AgentDeSecurite> existingAgent = agentRepository.findById(id);
        if (existingAgent.isPresent()) {
            AgentDeSecurite agent = existingAgent.get();
            agent.setNom(updatedAgent.getNom());
            agent.setPrenom(updatedAgent.getPrenom());
            agent.setEmail(updatedAgent.getEmail());
            agent.setTelephone(updatedAgent.getTelephone());
            agent.setAdresse(updatedAgent.getAdresse());
            agent.setDateNaissance(updatedAgent.getDateNaissance());
            agent.setSalaire(updatedAgent.getSalaire());
            agent.setZoneDeTravail(updatedAgent.getZoneDeTravail());
            agent.setStatut(updatedAgent.getStatut());
            return agentRepository.save(agent);
        }
        return null;
    }

    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }


}
