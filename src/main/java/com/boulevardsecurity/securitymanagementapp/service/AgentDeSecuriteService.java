package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.model.ZoneDeTravail;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.ZoneDeTravailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AgentDeSecuriteService {

    private final AgentDeSecuriteRepository agentRepository;
    private final ZoneDeTravailRepository zoneDeTravailRepository;


    // ✅ Récupérer tous les agents de sécurité
    public List<AgentDeSecurite> getAllAgents() {
        return agentRepository.findAll();
    }

    // ✅ Récupérer un agent par son ID
    public Optional<AgentDeSecurite> getAgentById(Long id) {
        return agentRepository.findById(id).map(agent -> {
            agent.getMissions().size(); // Force le chargement des missions (Hibernate Lazy Loading)
            return agent;
        });
    }

    // ✅ Ajouter ou mettre à jour un agent
    public AgentDeSecurite saveAgent(AgentDeSecurite agent) {
        return agentRepository.save(agent);
    }

    // ✅ Mettre à jour un agent existant
    public AgentDeSecurite updateAgent(Long id, AgentDeSecurite updatedAgent) {
        return agentRepository.findById(id)
                .map(existingAgent -> {
                    existingAgent.setNom(updatedAgent.getNom());
                    existingAgent.setPrenom(updatedAgent.getPrenom());
                    existingAgent.setEmail(updatedAgent.getEmail());
                    existingAgent.setTelephone(updatedAgent.getTelephone());
                    existingAgent.setAdresse(updatedAgent.getAdresse());
                    existingAgent.setDateNaissance(updatedAgent.getDateNaissance());
                    existingAgent.setStatut(updatedAgent.getStatut());

                    // 🔹 Mise à jour des zones de travail
                    if (updatedAgent.getZonesDeTravail() != null) {
                        existingAgent.setZonesDeTravail(updatedAgent.getZonesDeTravail());
                    }

                    // 🔹 Mise à jour des disponibilités
                    if (updatedAgent.getDisponibilites() != null) {
                        existingAgent.setDisponibilites(updatedAgent.getDisponibilites());
                    }

                    return agentRepository.save(existingAgent);
                })
                .orElseThrow(() -> new RuntimeException("Agent de sécurité non trouvé avec l'id : " + id));
    }

    // ✅ Supprimer un agent par ID
    public void deleteAgent(Long id) {
        if (agentRepository.existsById(id)) {
            agentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Agent de sécurité non trouvé avec l'id : " + id);
        }
    }

    // ✅ Récupérer le planning d’un agent par ses missions
    public Optional<Planning> getPlanningByAgentId(Long agentId) {
        return agentRepository.findFirstByMissions_Agents_IdOrderByMissions_DateDebutDesc(agentId);
    }

    // Méthode pour associer une zone de travail à un agent
    public AgentDeSecurite assignerZoneDeTravail(Long agentId, Long zoneId) {
        // Récupérer l'agent par son ID
        Optional<AgentDeSecurite> agentOpt = agentRepository.findById(agentId);

        if (agentOpt.isPresent()) {
            AgentDeSecurite agent = agentOpt.get();

            // Récupérer la zone de travail par son ID
            Optional<ZoneDeTravail> zoneOpt = zoneDeTravailRepository.findById(zoneId);

            if (zoneOpt.isPresent()) {
                ZoneDeTravail zone = zoneOpt.get();

                // Ajouter la zone de travail à l'agent
                agent.getZonesDeTravail().add(zone);

                // Sauvegarder l'agent après l'ajout de la zone de travail
                agentRepository.save(agent);

                return agent;
            } else {
                throw new RuntimeException("Zone de travail non trouvée avec l'id : " + zoneId);
            }
        } else {
            throw new RuntimeException("Agent non trouvé avec l'id : " + agentId);
        }
    }

}
