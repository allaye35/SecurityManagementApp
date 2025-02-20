package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.RapportIntervention;
import com.boulevardsecurity.securitymanagementapp.service.RapportInterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rapports")
public class RapportInterventionController {

    @Autowired
    private RapportInterventionService rapportInterventionService;

    @GetMapping
    public List<RapportIntervention> getAllRapports() {
        return rapportInterventionService.getAllRapports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportIntervention> getRapportById(@PathVariable Long id) {
        Optional<RapportIntervention> rapport = rapportInterventionService.getRapportById(id);
        return rapport.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public RapportIntervention createRapport(@RequestBody RapportIntervention rapport) {
        return rapportInterventionService.createRapport(rapport);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RapportIntervention> updateRapport(@PathVariable Long id, @RequestBody RapportIntervention updatedRapport) {
        RapportIntervention rapport = rapportInterventionService.updateRapport(id, updatedRapport);
        return rapport != null ? ResponseEntity.ok(rapport) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        rapportInterventionService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }
}
