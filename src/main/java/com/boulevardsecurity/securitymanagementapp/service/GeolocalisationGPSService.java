package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.GeolocalisationGPS;
import com.boulevardsecurity.securitymanagementapp.repository.GeolocalisationGPSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeolocalisationGPSService {

    @Autowired
    private GeolocalisationGPSRepository geolocalisationGPSRepository;

    public List<GeolocalisationGPS> getAllGeolocalisations() {
        return geolocalisationGPSRepository.findAll();
    }

    public Optional<GeolocalisationGPS> getGeolocalisationById(Long id) {
        return geolocalisationGPSRepository.findById(id);
    }

    public GeolocalisationGPS createGeolocalisation(GeolocalisationGPS geolocalisationGPS) {
        return geolocalisationGPSRepository.save(geolocalisationGPS);
    }

    public GeolocalisationGPS updateGeolocalisation(Long id, GeolocalisationGPS updatedGeolocalisation) {
        return geolocalisationGPSRepository.findById(id).map(geo -> {
            geo.setLatitude(updatedGeolocalisation.getLatitude());
            geo.setLongitude(updatedGeolocalisation.getLongitude());
            return geolocalisationGPSRepository.save(geo);
        }).orElse(null);
    }

    public void deleteGeolocalisation(Long id) {
        geolocalisationGPSRepository.deleteById(id);
    }
}
