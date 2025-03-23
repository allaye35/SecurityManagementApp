package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.GeoPoint;
import com.boulevardsecurity.securitymanagementapp.model.GeolocalisationGPS;
import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.Pointage;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import com.boulevardsecurity.securitymanagementapp.repository.PointageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointageService {

    private final PointageRepository pointageRepository;
    private final MissionRepository missionRepository;

    /**
     * Créer un nouveau pointage, en vérifiant la position GPS de l'agent
     * par rapport à celle de la Mission.
     */
    public Pointage creerPointage(Pointage pointage) {
        // 1) Vérifier que la mission existe
        Mission mission = missionRepository.findById(pointage.getMission().getId())
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable !"));

        // 2) Vérifier la position GPS de la mission
        GeolocalisationGPS geoMission = mission.getGeolocalisationGPS();
        if (geoMission == null || geoMission.getPosition() == null) {
            throw new IllegalArgumentException("La mission n'a pas de position GPS définie !");
        }

        // 3) Vérifier la position de l'agent par rapport à la mission (tolérance 100 m par exemple)
        float toleranceMetres = 100f;
        if (!verifierPositionPointage(geoMission.getPosition(), pointage.getPositionActuelle(), toleranceMetres)) {
            throw new IllegalArgumentException("❌ L'agent n'est pas dans la zone autorisée pour pointer !");
        }

        // 4) Enregistrer le pointage
        pointage.setDatePointage(new Date());
        pointage.setMission(mission);

        return pointageRepository.save(pointage);
    }

    /**
     * Vérifier la proximité entre la position de la mission et la position de l'agent
     */
    private boolean verifierPositionPointage(GeoPoint missionPos, GeoPoint agentPos, float toleranceMetres) {
        double distance = calculerDistanceEnMetres(missionPos, agentPos);
        return distance <= toleranceMetres;
    }

    /**
     * Calcul de la distance entre deux points (latitude/longitude) en mètres
     * (Formule du Haversine)
     */
    private double calculerDistanceEnMetres(GeoPoint p1, GeoPoint p2) {
        final int R = 6371000; // Rayon de la Terre en mètres

        double lat1 = Math.toRadians(p1.getLatitude());
        double lat2 = Math.toRadians(p2.getLatitude());
        double dLat = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double dLon = Math.toRadians(p2.getLongitude() - p1.getLongitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance en mètres
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // CRUD et autres méthodes
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Récupérer tous les pointages
     */
    public List<Pointage> obtenirTousLesPointages() {
        return pointageRepository.findAll();
    }

    /**
     * Récupérer un pointage par ID
     */
    public Optional<Pointage> obtenirPointageParId(Long id) {
        return pointageRepository.findById(id);
    }

    /**
     * Récupérer tous les pointages d'une mission
     */
    public List<Pointage> obtenirPointagesParMission(Long missionId) {
        return pointageRepository.findByMissionId(missionId);
    }

    /**
     * Mettre à jour un pointage existant (on ne refait pas la vérification si on suppose
     * qu'on ne change pas la positionActuelle)
     */
    public Pointage mettreAJourPointage(Long id, Pointage nouveauPointage) {
        Pointage pointageExistant = pointageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pointage non trouvé !"));

        // On met à jour seulement certains champs
        pointageExistant.setEstPresent(nouveauPointage.isEstPresent());
        pointageExistant.setEstRetard(nouveauPointage.isEstRetard());
        pointageExistant.setPositionActuelle(nouveauPointage.getPositionActuelle());
        pointageExistant.setMission(nouveauPointage.getMission());

        return pointageRepository.save(pointageExistant);
    }

    /**
     * Supprimer un pointage
     */
    public void supprimerPointage(Long id) {
        if (!pointageRepository.existsById(id)) {
            throw new NoSuchElementException("Pointage non trouvé !");
        }
        pointageRepository.deleteById(id);
    }
}
