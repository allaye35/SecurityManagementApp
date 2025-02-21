package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.repository.PlanningRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class PlanningService {

    private final PlanningRepository planningRepository;

    public PlanningService(PlanningRepository planningRepository) {
        this.planningRepository = planningRepository;
    }

    // ✅ Ajouter un agent au planning
    public Planning ajouterAgent(Long planningId, AgentDeSecurite agent) {
        Optional<Planning> planningOpt = planningRepository.findById(planningId);
        if (planningOpt.isPresent()) {
            Planning planning = planningOpt.get();
            planning.getAgents().add(agent);
            return planningRepository.save(planning);
        }
        throw new RuntimeException("Planning non trouvé !");
    }

    // ✅ Supprimer un agent du planning
    public Planning supprimerAgent(Long planningId, AgentDeSecurite agent) {
        Optional<Planning> planningOpt = planningRepository.findById(planningId);
        if (planningOpt.isPresent()) {
            Planning planning = planningOpt.get();
            planning.getAgents().remove(agent);
            return planningRepository.save(planning);
        }
        throw new RuntimeException("Planning non trouvé !");
    }

    // ✅ Ajouter une mission au planning
    public Planning ajouterMission(Long planningId, Mission mission) {
        Optional<Planning> planningOpt = planningRepository.findById(planningId);
        if (planningOpt.isPresent()) {
            Planning planning = planningOpt.get();
            mission.setPlanning(planning);
            planning.getMissions().add(mission);
            return planningRepository.save(planning);
        }
        throw new RuntimeException("Planning non trouvé !");
    }

    // ✅ Supprimer une mission du planning
    public Planning supprimerMission(Long planningId, Mission mission) {
        Optional<Planning> planningOpt = planningRepository.findById(planningId);
        if (planningOpt.isPresent()) {
            Planning planning = planningOpt.get();
            planning.getMissions().remove(mission);
            mission.setPlanning(null);
            return planningRepository.save(planning);
        }
        throw new RuntimeException("Planning non trouvé !");
    }

    // ✅ Obtenir le nombre d'agents dans un planning
    public int getNombreAgents(Long planningId) {
        return planningRepository.findById(planningId)
                .map(planning -> planning.getAgents().size())
                .orElseThrow(() -> new RuntimeException("Planning non trouvé !"));
    }

    // ✅ Obtenir le nombre de missions dans un planning
    public int getNombreMissions(Long planningId) {
        return planningRepository.findById(planningId)
                .map(planning -> planning.getMissions().size())
                .orElseThrow(() -> new RuntimeException("Planning non trouvé !"));
    }
}
