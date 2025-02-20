package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    @Autowired
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @GetMapping("/listesmissions")
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }

    @GetMapping("/uneMission/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);
        return mission != null ? ResponseEntity.ok(mission) : ResponseEntity.notFound().build();
    }

    @PostMapping("/ajouterMission")
    public Mission createMission(@RequestBody Mission mission) {
        return missionService.createMission(mission);
    }

    @PutMapping("/modifierMission/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission updatedMission) {
        Mission mission = missionService.updateMission(id, updatedMission);
        return mission != null ? ResponseEntity.ok(mission) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/supprimerMission/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
}
