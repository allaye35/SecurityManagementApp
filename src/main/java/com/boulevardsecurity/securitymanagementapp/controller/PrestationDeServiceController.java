package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.PrestationDeService;
import com.boulevardsecurity.securitymanagementapp.service.PrestationDeServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prestations")
public class PrestationDeServiceController {

    private final PrestationDeServiceService prestationService;

    public PrestationDeServiceController(PrestationDeServiceService prestationService) {
        this.prestationService = prestationService;
    }

    @GetMapping
    public List<PrestationDeService> getAllPrestations() {
        return prestationService.getAllPrestations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestationDeService> getPrestationById(@PathVariable Long id) {
        Optional<PrestationDeService> prestation = prestationService.getPrestationById(id);
        return prestation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PrestationDeService createPrestation(@RequestBody PrestationDeService prestation) {
        return prestationService.createPrestation(prestation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestationDeService> updatePrestation(@PathVariable Long id, @RequestBody PrestationDeService updatedPrestation) {
        PrestationDeService prestation = prestationService.updatePrestation(id, updatedPrestation);
        return prestation != null ? ResponseEntity.ok(prestation) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrestation(@PathVariable Long id) {
        return prestationService.deletePrestation(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
