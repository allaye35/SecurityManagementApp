package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.repository.PlanningRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanningService {

    private final PlanningRepository planningRepository;

    public PlanningService(PlanningRepository planningRepository) {
        this.planningRepository = planningRepository;
    }

    public List<Planning> getAllPlannings() {
        return planningRepository.findAll();
    }

    public Planning getPlanningById(Long id) {
        return planningRepository.findById(id).orElse(null);
    }

    public Planning createPlanning(Planning planning) {
        return planningRepository.save(planning);
    }

    public Planning updatePlanning(Long id, Planning updatedPlanning) {
        Optional<Planning> existingPlanning = planningRepository.findById(id);
        if (existingPlanning.isPresent()) {
            Planning planning = existingPlanning.get();
            planning.setDate(updatedPlanning.getDate());
            planning.setHeureDebut(updatedPlanning.getHeureDebut());
            planning.setHeureFin(updatedPlanning.getHeureFin());
            return planningRepository.save(planning);
        }
        return null;
    }

    public void deletePlanning(Long id) {
        planningRepository.deleteById(id);
    }
}
