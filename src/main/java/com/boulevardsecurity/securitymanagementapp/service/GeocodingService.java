package com.boulevardsecurity.securitymanagementapp.service;
import com.boulevardsecurity.securitymanagementapp.model.GeoPoint;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public GeoPoint getCoordinatesFromAddress(String address) {
        try {
            // Construire l’URL pour l’appel Nominatim
            String url = NOMINATIM_URL + address.replace(" ", "+");

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Nominatim renvoie un tableau JSON
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                throw new IllegalArgumentException("Adresse introuvable via Nominatim : " + address);
            }

            // Récupérer le premier résultat
            JSONObject location = array.getJSONObject(0);
            double lat = location.getDouble("lat");
            double lon = location.getDouble("lon");

            log.info("Adresse {} => lat: {}, lon: {}", address, lat, lon);

            return new GeoPoint(lat, lon);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la géolocalisation de l’adresse : " + address, e);
        }
    }
}

