package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import com.boulevardsecurity.securitymanagementapp.repository.PlanningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // 🔹 Créer ou mettre à jour un planning
    public Planning savePlanning(Planning planning) {
        return planningRepository.save(planning);
    }

    // 🔹 Supprimer un planning par ID
    public void deletePlanning(Long id) {
        planningRepository.deleteById(id);
    }

    // 🔹 Ajouter une mission à un planning existant
    public Planning ajouterMissionAuPlanning(Long planningId, Long missionId) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        mission.setPlanning(planning);
        planning.getMissions().add(mission);
        return planningRepository.save(planning);
    }

    public void retirerMissionDuPlanning(Long planningId, Long missionId) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        // Vérifier si la mission appartient bien à ce planning
        if (mission.getPlanning() != null && mission.getPlanning().getId().equals(planningId)) {
            mission.setPlanning(null); // Dissocier la mission du planning
            missionRepository.save(mission); // Mise à jour de la mission
        } else {
            throw new RuntimeException("Mission non associée à ce planning !");
        }
    }




    // 🔹 Récupérer les plannings d'un agent
    public List<Planning> getPlanningsByAgent(Long agentId) {
        return planningRepository.findByMissions_Agents_Id(agentId);
    }

    // 🔹 Filtrer plannings par période (ex : semaine/mois)
    public List<Planning> getPlanningsByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return planningRepository.findByDateCreationBetween(dateDebut, dateFin);
    }




    // 🔹 Recherche planning par mission spécifique
    public List<Planning> getPlanningsByMission(Long missionId) {
        return planningRepository.findByMissions_Id(missionId);
    }

}
