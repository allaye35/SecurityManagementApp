package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MissionService {

    private final MissionRepository missionRepository;

    @Autowired
    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Mission getMissionById(Long id) {
        return missionRepository.findById(id).orElse(null);
    }

    public Mission createMission(Mission mission) {
        return missionRepository.save(mission);
    }

    public Mission updateMission(Long id, Mission updatedMission) {
        Optional<Mission> existingMission = missionRepository.findById(id);
        if (existingMission.isPresent()) {
            Mission mission = existingMission.get();
            mission.setTitre(updatedMission.getTitre());
            mission.setDescription(updatedMission.getDescription());
            mission.setDateDebut(updatedMission.getDateDebut());
            mission.setDateFin(updatedMission.getDateFin());
            mission.setEntreprise(updatedMission.getEntreprise());
            mission.setPlanning(updatedMission.getPlanning());
            return missionRepository.save(mission);
        }
        return null;
    }

    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    public List<Mission> getMissionsByEntreprise(Long entrepriseId) {
        return missionRepository.findByEntrepriseId(entrepriseId);
    }

    public List<Mission> getMissionsEnCours() {
        return missionRepository.findByDateDebutAfter(LocalDate.now());
    }

    public List<Mission> getMissionsTerminees() {
        return missionRepository.findByDateFinBefore(LocalDate.now());
    }
}
