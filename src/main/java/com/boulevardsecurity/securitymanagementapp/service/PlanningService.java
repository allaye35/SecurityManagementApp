package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import com.boulevardsecurity.securitymanagementapp.repository.PlanningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final MissionRepository missionRepository;

    // 🔹 Récupérer tous les plannings
    public List<Planning> getAllPlannings() {
        return planningRepository.findAll();
    }

    // 🔹 Récupérer un planning par ID
    public Optional<Planning> getPlanningById(Long id) {
        return planningRepository.findById(id);
    }

    // 🔹 Ajouter ou mettre à jour un planning
    public Planning savePlanning(Planning planning) {
        return planningRepository.save(planning);
    }

    // 🔹 Supprimer un planning par ID
    public void deletePlanning(Long id) {
        planningRepository.deleteById(id);
    }


}
