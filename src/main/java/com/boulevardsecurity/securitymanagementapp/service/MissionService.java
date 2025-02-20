package com.boulevardsecurity.securitymanagementapp.service;


import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<Mission> missionOpt = missionRepository.findById(id);
        if (missionOpt.isPresent()) {
            Mission mission = missionOpt.get();
            mission.setTitre(updatedMission.getTitre());
            mission.setDescription(updatedMission.getDescription());
            mission.setDateDebut(updatedMission.getDateDebut());
            mission.setDateFin(updatedMission.getDateFin());
            return missionRepository.save(mission);
        }
        return null;
    }

    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }
}
