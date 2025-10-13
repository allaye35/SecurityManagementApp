package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.GeoPoint;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public GeoPoint getCoordinatesFromAddress(String address) {
        try {
            String url = NOMINATIM_URL + address.replace(" ", "+");

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "BoulevardSecurityApp/1.0 (contact@boulevardsecurity.com)");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, entity);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur HTTP lors de la géolocalisation : " + response.getStatusCode());
            }

            JSONArray array = new JSONArray(response.getBody());
            if (array.length() == 0) {
                throw new IllegalArgumentException("Adresse introuvable via Nominatim : " + address);
            }

            JSONObject location = array.getJSONObject(0);
            double lat = location.getDouble("lat");
            double lon = location.getDouble("lon");

            log.info("Adresse '{}' trouvée : lat={}, lon={}", address, lat, lon);

            return new GeoPoint(lat, lon);
        } catch (Exception e) {
            log.error(" Erreur lors de la géolocalisation de l’adresse : {}", address, e);
            throw new RuntimeException("Erreur lors de la géolocalisation de l’adresse : " + address, e);
        }
    }
}
