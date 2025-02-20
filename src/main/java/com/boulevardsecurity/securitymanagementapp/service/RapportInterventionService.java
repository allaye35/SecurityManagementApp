package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.RapportIntervention;
import com.boulevardsecurity.securitymanagementapp.repository.RapportInterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RapportInterventionService {

    @Autowired
    private RapportInterventionRepository rapportInterventionRepository;

    public List<RapportIntervention> getAllRapports() {
        return rapportInterventionRepository.findAll();
    }

    public Optional<RapportIntervention> getRapportById(Long id) {
        return rapportInterventionRepository.findById(id);
    }

    public RapportIntervention createRapport(RapportIntervention rapport) {
        return rapportInterventionRepository.save(rapport);
    }

    public RapportIntervention updateRapport(Long id, RapportIntervention updatedRapport) {
        return rapportInterventionRepository.findById(id).map(rapport -> {
            rapport.setDateIntervention(updatedRapport.getDateIntervention());
            rapport.setDescription(updatedRapport.getDescription());
            rapport.setAgentNom(updatedRapport.getAgentNom());
            rapport.setStatus(updatedRapport.getStatus());
            return rapportInterventionRepository.save(rapport);
        }).orElse(null);
    }

    public void deleteRapport(Long id) {
        rapportInterventionRepository.deleteById(id);
    }
}
