package com.boulevardsecurity.securitymanagementapp.controller;
import com.boulevardsecurity.securitymanagementapp.service.GeoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geo")
public class GeoApiController {

    @Autowired
    private GeoApiService geoApiService;

    //  Récupérer toutes les régions
    @GetMapping("/regions")
    public ResponseEntity<List<Map<String, Object>>> getRegions() {
        return ResponseEntity.ok(geoApiService.getRegions());
    }

    // Récupérer tous les départements d'une région
    @GetMapping("/regions/{codeRegion}/departements")
    public ResponseEntity<List<Map<String, Object>>> getDepartements(@PathVariable String codeRegion) {
        return ResponseEntity.ok(geoApiService.getDepartementsByRegion(codeRegion));
    }

    //  Récupérer toutes les communes d'un département
    @GetMapping("/departements/{codeDepartement}/communes")
    public ResponseEntity<List<Map<String, Object>>> getCommunes(@PathVariable String codeDepartement) {
        return ResponseEntity.ok(geoApiService.getCommunesByDepartement(codeDepartement));
    }

    //  Récupérer une commune par code postal
    @GetMapping("/commune")
    public ResponseEntity<Map<String, Object>> getCommuneByCodePostal(@RequestParam String codePostal) {
        Map<String, Object> commune = geoApiService.getCommuneByCodePostal(codePostal);
        if (commune != null) {
            return ResponseEntity.ok(commune);
        }
        return ResponseEntity.notFound().build();
    }

    //  Récupérer une commune par nom
    @GetMapping("/communes/nom")
    public ResponseEntity<List<Map<String, Object>>> getCommunesByNom(@RequestParam String nom) {
        return ResponseEntity.ok(geoApiService.getCommunesByNom(nom));
    }

    //  Récupérer un département par code
    @GetMapping("/departement/{code}")
    public ResponseEntity<Map<String, Object>> getDepartementByCode(@PathVariable String code) {
        return ResponseEntity.ok(geoApiService.getDepartementByCode(code));
    }

    //  Récupérer une région par code
    @GetMapping("/region/{code}")
    public ResponseEntity<Map<String, Object>> getRegionByCode(@PathVariable String code) {
        return ResponseEntity.ok(geoApiService.getRegionByCode(code));
    }
}

