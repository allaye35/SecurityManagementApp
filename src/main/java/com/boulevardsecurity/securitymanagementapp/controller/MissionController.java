package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "http://localhost:3000")

public class MissionController {


    private final MissionService missionService;
    @Autowired
    MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    // 🔹 Récupérer toutes les missions
    @GetMapping
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }

    // 🔹 Récupérer une mission par ID
    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        Optional<Mission> mission = missionService.getMissionById(id);
        return mission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter une mission
//    @PostMapping
//    public Mission createMission(@RequestBody Mission mission) {
//        return missionService.saveMission(mission);
//    }

    @PostMapping
    public ResponseEntity<Mission> createMission(@Valid @RequestBody Mission mission) {
        Mission createdMission = missionService.saveMission(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMission);
    }


    // 🔹 Modifier une mission existante
    @PutMapping("/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission updatedMission) {
        return missionService.getMissionById(id).map(existingMission -> {
            existingMission.setTitre(updatedMission.getTitre());
            existingMission.setDescription(updatedMission.getDescription());
            existingMission.setDateDebut(updatedMission.getDateDebut());
            existingMission.setDateFin(updatedMission.getDateFin());
            existingMission.setStatutMission(updatedMission.getStatutMission());
            existingMission.setSite(updatedMission.getSite()); // 🔹 Ajout du site
            return ResponseEntity.ok(missionService.saveMission(existingMission));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer une mission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        if (missionService.getMissionById(id).isPresent()) {
            missionService.deleteMission(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
