package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.RapportIntervention;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import com.boulevardsecurity.securitymanagementapp.repository.RapportInterventionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RapportInterventionService {

    private final RapportInterventionRepository rapportRepository;
    private final MissionRepository missionRepository;

    // 🔹 Récupérer tous les rapports
    public List<RapportIntervention> getAllRapports() {
        return rapportRepository.findAll();
    }

    // 🔹 Récupérer un rapport par ID
    public Optional<RapportIntervention> getRapportById(Long id) {
        return rapportRepository.findById(id);
    }

    // 🔹 Récupérer les rapports d'une mission
    public List<RapportIntervention> getRapportsByMissionId(Long missionId) {
        return rapportRepository.findByMissionId(missionId);
    }

    // 🔹 Créer un rapport et l'associer à une mission
    public RapportIntervention createRapport(Long missionId, RapportIntervention rapport) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée avec ID: " + missionId));

        rapport.setMission(mission);
        return rapportRepository.save(rapport);
    }

    // 🔹 Modifier un rapport existant
    public RapportIntervention updateRapport(Long id, RapportIntervention updatedRapport) {
        return rapportRepository.findById(id).map(existingRapport -> {
            existingRapport.setDateIntervention(updatedRapport.getDateIntervention());
            existingRapport.setDescription(updatedRapport.getDescription());
            existingRapport.setAgentNom(updatedRapport.getAgentNom());
            existingRapport.setDateModification(updatedRapport.getDateModification());
            existingRapport.setAgentEmail(updatedRapport.getAgentEmail());
            existingRapport.setAgentTelephone(updatedRapport.getAgentTelephone());
            existingRapport.setContenu(updatedRapport.getContenu());
            existingRapport.setStatus(updatedRapport.getStatus());
            return rapportRepository.save(existingRapport);
        }).orElseThrow(() -> new RuntimeException("Rapport non trouvé avec ID: " + id));
    }

    // 🔹 Supprimer un rapport
    public void deleteRapport(Long id) {
        rapportRepository.deleteById(id);
    }
}
