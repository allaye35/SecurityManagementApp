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
        Optional<AgentDeSecurite> existingAgentOpt = agentDeSecuriteRepository.findById(id);

        if (existingAgentOpt.isPresent()) {
            AgentDeSecurite agent = existingAgentOpt.get();
            agent.setNom(updatedAgent.getNom());
            agent.setPrenom(updatedAgent.getPrenom());
            agent.setEmail(updatedAgent.getEmail());
            agent.setTelephone(updatedAgent.getTelephone());
            agent.setAdresse(updatedAgent.getAdresse());
            agent.setDateNaissance(updatedAgent.getDateNaissance());
            agent.setZoneDeTravail(updatedAgent.getZoneDeTravail());
            agent.setSalaire(updatedAgent.getSalaire());
            agent.setActif(updatedAgent.isActif());
            System.out.println(agent);
            return agentDeSecuriteRepository.save(agent);
        } else {
            return null;  // Pour éviter de sauvegarder un agent inexistant
        }
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
