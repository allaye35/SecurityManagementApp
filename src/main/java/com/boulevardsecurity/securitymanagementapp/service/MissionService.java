package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    // 🔹 Récupérer toutes les missions
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    // 🔹 Récupérer une mission par ID
    public Optional<Mission> getMissionById(Long id) {
        return missionRepository.findById(id);
    }

    // 🔹 Ajouter ou mettre à jour une mission
    public Mission saveMission(Mission mission) {
        return missionRepository.save(mission);
    }

    // 🔹 Supprimer une mission par ID
    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }
}
