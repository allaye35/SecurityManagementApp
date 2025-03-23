package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.GeolocalisationGPS;
import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.service.GeolocalisationGPSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geolocalisations")
@CrossOrigin(origins = "http://localhost:3000") // Autorise les requêtes depuis React
public class GeolocalisationGPSController {

    private final GeolocalisationGPSService service;

    public GeolocalisationGPSController(GeolocalisationGPSService service) {
        this.service = service;
    }

    @GetMapping
    public List<GeolocalisationGPS> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeolocalisationGPS> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PutMapping("/{id}")
    public GeolocalisationGPS update(@PathVariable Long id, @RequestBody GeolocalisationGPS gps) {
        return service.update(id, gps);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @PostMapping
    public GeolocalisationGPS create(@RequestBody GeolocalisationGPS gps) {
        return service.create(gps);
    }

    // ✅ Nouveau endpoint : Ajouter une mission à une géolocalisation
    @PostMapping("/{gpsId}/missions")
    public ResponseEntity<Mission> addMissionToGeolocalisation(@PathVariable Long gpsId, @RequestBody Mission mission) {
        return ResponseEntity.ok(service.addMissionToGeolocalisation(gpsId, mission));
    }
}
