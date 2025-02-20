package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.GeolocalisationGPS;
import com.boulevardsecurity.securitymanagementapp.service.GeolocalisationGPSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/geolocalisationsGPS")
public class GeolocalisationGPSController {

    @Autowired
    private GeolocalisationGPSService geolocalisationGPSService;

    @GetMapping
    public List<GeolocalisationGPS> getAllGeolocalisations() {
        return geolocalisationGPSService.getAllGeolocalisations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeolocalisationGPS> getGeolocalisationById(@PathVariable Long id) {
        Optional<GeolocalisationGPS> geo = geolocalisationGPSService.getGeolocalisationById(id);
        return geo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public GeolocalisationGPS createGeolocalisation(@RequestBody GeolocalisationGPS geolocalisationGPS) {
        return geolocalisationGPSService.createGeolocalisation(geolocalisationGPS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeolocalisationGPS> updateGeolocalisation(@PathVariable Long id, @RequestBody GeolocalisationGPS updatedGeolocalisation) {
        GeolocalisationGPS geo = geolocalisationGPSService.updateGeolocalisation(id, updatedGeolocalisation);
        return geo != null ? ResponseEntity.ok(geo) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeolocalisation(@PathVariable Long id) {
        geolocalisationGPSService.deleteGeolocalisation(id);
        return ResponseEntity.noContent().build();
    }
}
