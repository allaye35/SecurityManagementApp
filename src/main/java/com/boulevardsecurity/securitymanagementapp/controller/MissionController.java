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

    @GetMapping("/listes")
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);
        return mission != null ? ResponseEntity.ok(mission) : ResponseEntity.notFound().build();
    }

    @PostMapping("/ajouter")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        Mission newMission = missionService.createMission(mission);
        return ResponseEntity.ok(newMission);
    }

    @PutMapping("/modifier/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission updatedMission) {
        Mission mission = missionService.updateMission(id, updatedMission);
        return mission != null ? ResponseEntity.ok(mission) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public List<Mission> getMissionsByEntreprise(@PathVariable Long entrepriseId) {
        return missionService.getMissionsByEntreprise(entrepriseId);
    }

    @GetMapping("/encours")
    public List<Mission> getMissionsEnCours() {
        return missionService.getMissionsEnCours();
    }

    @GetMapping("/terminees")
    public List<Mission> getMissionsTerminees() {
        return missionService.getMissionsTerminees();
    }
}
