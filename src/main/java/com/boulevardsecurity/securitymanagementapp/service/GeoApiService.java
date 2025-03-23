package com.boulevardsecurity.securitymanagementapp.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class GeoApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://geo.api.gouv.fr/";

    //  Récupérer toutes les régions
    public List<Map<String, Object>> getRegions() {
        String url = BASE_URL + "regions";
        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
        return Arrays.asList(response.getBody());
    }

    //  Récupérer tous les départements d'une région
    public List<Map<String, Object>> getDepartementsByRegion(String codeRegion) {
        String url = BASE_URL + "regions/" + codeRegion + "/departements";
        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
        return Arrays.asList(response.getBody());
    }

    //  Récupérer toutes les communes d'un département
    public List<Map<String, Object>> getCommunesByDepartement(String codeDepartement) {
        String url = BASE_URL + "departements/" + codeDepartement + "/communes";
        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
        return Arrays.asList(response.getBody());
    }

    //  Récupérer une commune par son code postal
    public Map<String, Object> getCommuneByCodePostal(String codePostal) {
        String url = BASE_URL + "communes?codePostal=" + codePostal;
        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);

        if (response.getBody() != null && response.getBody().length > 0) {
            return response.getBody()[0];
        }
        return null;
    }

    //  Récupérer une commune par son nom (ville)
    public List<Map<String, Object>> getCommunesByNom(String nom) {
        String url = BASE_URL + "communes?nom=" + nom;
        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);

        if (response.getBody() != null) {
            return Arrays.asList(response.getBody());
        }
        return null;
    }

    //  Récupérer un département par son code
    public Map<String, Object> getDepartementByCode(String codeDepartement) {
        String url = BASE_URL + "departements/" + codeDepartement;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        return response.getBody();
    }

    //  Récupérer une région par son code
    public Map<String, Object> getRegionByCode(String codeRegion) {
        String url = BASE_URL + "regions/" + codeRegion;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        return response.getBody();
    }
}

