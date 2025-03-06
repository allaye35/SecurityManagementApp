package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.GeolocalisationGPS;
import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.repository.GeolocalisationGPSRepository;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocalisationGPSService {

    private final GeolocalisationGPSRepository gpsRepository;
    private final MissionRepository missionRepository;

    public GeolocalisationGPSService(GeolocalisationGPSRepository gpsRepository, MissionRepository missionRepository) {
        this.gpsRepository = gpsRepository;
        this.missionRepository = missionRepository;
    }

    public List<GeolocalisationGPS> getAll() {
        return gpsRepository.findAll();
    }

    public GeolocalisationGPS getById(Long id) {
        return gpsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GeolocalisationGPS non trouvée avec id: " + id));
    }

    public GeolocalisationGPS create(GeolocalisationGPS gps) {
        return gpsRepository.save(gps);
    }

    public GeolocalisationGPS update(Long id, GeolocalisationGPS gpsDetails) {
        return gpsRepository.findById(id).map(gps -> {
            gps.setGps_precision(gpsDetails.getGps_precision());
            gps.setPosition(gpsDetails.getPosition());
            return gpsRepository.save(gps);
        }).orElseThrow(() -> new RuntimeException("GeolocalisationGPS non trouvée avec id: " + id));
    }

    public void delete(Long id) {
        gpsRepository.deleteById(id);
    }

    // ✅ Correction : Ajouter une mission à une géolocalisation
    public Mission addMissionToGeolocalisation(Long gpsId, Mission mission) {
        GeolocalisationGPS gps = gpsRepository.findById(gpsId)
                .orElseThrow(() -> new RuntimeException("GeolocalisationGPS non trouvée"));

        // Vérifier si la liste des missions est initialisée
        if (gps.getMissions() == null) {
            gps.setMissions(new ArrayList<>()); // Initialiser si null
        }

        // Ajouter la mission à la liste
        mission.setGeolocalisationGPS(gps);
        gps.getMissions().add(mission);

        // Sauvegarder les deux
        gpsRepository.save(gps);
        return missionRepository.save(mission);
    }
}
